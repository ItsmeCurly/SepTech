"""/web/app/syzygy/items/schema.py

Author: Adam Green (adam.green1@maine.edu)

[Description]

Classes:

    [ClassesList]

Functions:

    [FunctionsList]

"""

import logging

from .model import Item
from app.syzygy.items.categories import CategorySchema
from marshmallow_sqlalchemy import ModelSchema

from marshmallow import Schema, fields
import marshmallow as ma

log = logging.getLogger(__name__)


class ItemSchema(Schema):

    itemid = fields.Integer(dump_only=True)
    name = fields.String()
    quantity = fields.Integer()
    posted_at = fields.DateTime()
    seller = fields.Integer()
    price = fields.Number()
    can_buy = fields.Bool()
    can_bid = fields.Bool()
    highest_bid = fields.Number()
    highest_bid_user = fields.Integer()
    bidding_ends = fields.DateTime()
    quality = fields.String()
    category_id = fields.Integer(dump_only=True)
    category = fields.Nested(CategorySchema)

    thumbnail = fields.Integer()
    item_variants = fields.List(fields.Integer)
    description = fields.Str()
    # attributes =
