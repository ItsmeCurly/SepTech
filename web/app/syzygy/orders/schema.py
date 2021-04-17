"""/web/app/syzygy/users/schema.py

Author: Adam Green (adam.green1@maine.edu)

[Description]

Classes:

    [ClassesList]

Functions:

    [FunctionsList]

"""

import logging

from marshmallow import Schema, fields

from ..order_items.schema import OrderItemSchema

log = logging.getLogger(__name__)


class OrderSchema(Schema):
    id = fields.Number(dump_only=True)

    userid = fields.Number()

    ordered = fields.Boolean()

    date_created = fields.DateTime(dump_only=True)
    date_shipped = fields.DateTime(dump_only=True)
    date_delivered = fields.DateTime(dump_only=True)

    order_items = fields.List(fields.Nested(OrderItemSchema), dump_only=True)
