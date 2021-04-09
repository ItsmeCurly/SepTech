"""/web/app/syzygy/users/service.py

Author: Adam Green (adam.green1@maine.edu)

[Description]

Classes:

    [ClassesList]

Functions:

    [FunctionsList]

"""

import json
import logging
import re
import secrets
import smtplib
from datetime import datetime, timedelta
from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from typing import List

import bcrypt
from app import db
from app.globals import *
from libs.auth import encrypt_pw
from libs.response import ErrResponse, NormalResponse

from .interface import UserInterface
from .model import User

log = logging.getLogger(__name__)


class UserService:
    @staticmethod
    def get_all():
        """[summary]

        :return: [description]
        :rtype: [type]
        """
        return User.query.all()

    @staticmethod
    def get_by_id(id: int) -> User:
        """[summary]

        :param id: [description]
        :type id: int
        :return: [description]
        :rtype: [type]
        """
        user = User.query.get(id)

        return user

    @staticmethod
    def get_by_email(email: str) -> User:
        """[summary]

        :param email: [description]
        :type email: str
        :return: [description]
        :rtype: [type]
        """

        user = User.query.filter(User.email == email).first()

        return user

    @staticmethod
    def update(user: User, User_change_updates: UserInterface) -> User:
        """[summary]

        :param user: The User to update in the database
        :type user: User
        :param User_change_updates: Dictionary object containing the new changes
        to update the User model object with
        :type User_change_updates: UserInterface
        :return: The updated User model object
        :rtype: User
        """
        user.update(User_change_updates)
        user.modified_at = datetime.now()

        db.session.commit()
        return user

    @staticmethod
    def delete_by_id(id: int) -> List:
        """Deletes a user from the table with the specified id

        :param id: User's id
        :type id: int
        :return: List containing the deleted user, if found, otherwise an empty
        list
        :rtype: List
        """

        user = UserService.get_by_id(id)
        if not user:
            return []
        db.session.delete(user)
        db.session.commit()
        return [id]

    @staticmethod
    def create(new_attrs: UserInterface) -> User:
        """Creates a user object from the UserInterface TypedDict

        :param new_attrs: A dictionary with the input into a User model
        :type new_attrs: UserInterface
        :return: A new user object based on the input
        :rtype: User
        """

        user = UserService.get_by_email(new_attrs["email"])

        print(user)

        if user is not None:
            return ErrResponse("User with email already exists", 400)

        encrypted_pw = encrypt_pw(new_attrs["password"])

        phone_number_reformatted = new_attrs["phone_number"]

        # reformat phone number to remove extraneous (non-numeric) chars
        for c in ["(", ")", "-", " "]:
            if c in new_attrs["phone_number"]:
                phone_number_reformatted.replace(c, "")

        new_user = User(
            email=new_attrs["email"],
            password=encrypted_pw,
            full_name=new_attrs["full_name"],
            date_of_birth=new_attrs["date_of_birth"],
            created_at=datetime.now(),
            modified_at=datetime.now(),
            phone_number=phone_number_reformatted,
            password_salt=new_attrs["password_salt"],
        )

        db.session.add(new_user)
        db.session.commit()

        return new_user

    @staticmethod
    def login(email: str, password: str) -> User:
        """Checks user credentials against database. If a user is found, then
        send the user information back to the client.

        :param email: User's email
        :type email: str
        :param password: User's password
        :type password: str
        :return: User model from the table with the specified email and
        password
        :rtype: User
        """

        log.debug(f"email: {email}\tPassword: {password}")

        if not email:
            return ErrResponse("No email entered", 400)

        if not password:
            return ErrResponse("No password entered", 400)

        user = UserService.get_by_email(email)

        if user is None:
            log.info("No user was found for supplied email")
            return ErrResponse("Incorrect email", 400)

        if not bcrypt.checkpw(password.encode("utf-8"), user.password):
            log.info("No user was found for supplied password")
            return ErrResponse("Incorrect password", 400)

        log.info(f"User {user.id} was found and returned to client")

        # generate JWT token and concatenate

        return user

    @staticmethod
    def reset_password(email: str):
        user = UserService.get_by_email(email)

        if user is None:
            return ErrResponse("User does not exist", 400)

        user_changes: UserInterface = {
            "password_reset_code": UserService.gen_unique_reset_code(),
            "password_reset_timeout": datetime.now()
            + timedelta(minutes=PASSWORD_RESET_TIME),
        }

        user = UserService.update(user, user_changes)

        UserService.send_password_code_email(user)

        return NormalResponse(
            "Healthy",
            200,
        )

        # user_changes = UserInterface(
        #     "password_reset_code": UserService.gen_unique_reset_code(),
        #     "password_reset_timeout": datetime.now(),
        # )

        # UserService.update(user, user_changes)

    @staticmethod
    def gen_unique_reset_code():
        done = False

        while not done:
            code = secrets.token_hex(nbytes=3)
            for other_code in User.query.with_entities(
                User.password_reset_code, User.password_reset_timeout
            ):
                if code == other_code[0] and (
                    other_code[1] and other_code[1] > datetime.now()
                ):
                    break
                done = True
        return code

    @staticmethod
    def send_password_code_email(recipient: User):

        img_name = "assets/centauri_logo_resized.png"

        mail_content = f"""\
        Hello,<br><br>

        Your password reset code is {recipient.password_reset_code}. This password reset request will expire in 15 minutes.<br><br>

        If you did not request this password reset, you can safely ignore this email.<br><br><br><br><br><br>

        Centauri Developers @ Septech Inc.<br><br>
        """
        # The mail addresses and password
        sender_address = "septech.centauri@gmail.com"
        sender_pass = "cos420umaine"

        receiver_address = recipient.email

        # Setup the MIME
        msg = MIMEMultipart()
        msg["From"] = sender_address
        msg["To"] = receiver_address
        msg["Subject"] = "Centauri Password Reset Code"

        # The body and the attachments for the mail
        # message.attach(MIMEText(mail_content, "plain"))

        msgText = MIMEText(
            '<b>%s</b><br><img src="cid:%s"><br>' % (mail_content, img_name), "html"
        )
        msg.attach(msgText)  # Added, and edited the previous line

        fp = open(img_name, "rb")
        img = MIMEImage(fp.read())
        fp.close()
        img.add_header("Content-ID", "<{}>".format(img_name))
        msg.attach(img)

        # Create SMTP session for sending the mail
        session = smtplib.SMTP("smtp.gmail.com", 587)  # use gmail with port
        session.starttls()  # enable security
        session.login(sender_address, sender_pass)  # login with mail_id and password
        text = msg.as_string()
        session.sendmail(sender_address, receiver_address, text)
        session.quit()

        print(f"Mail sent to {recipient.email} at {datetime.now()}")

    @staticmethod
    def verify_code(code: str, email: str) -> bool:
        user = UserService.get_by_email(email)

        return (
            NormalResponse("Confirmed", 200)
            if code.strip() == user.password_reset_code
            else ErrResponse("Wrong Code", 400)
        )

    @staticmethod
    def check_exists(email: str) -> bool:
        user = UserService.get_by_email(email)

        return (
            NormalResponse("User does not exist", 200)
            if user is None
            else ErrResponse("User exists", 400)
        )