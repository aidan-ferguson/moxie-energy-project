# Django REST framework seralisers
# Based on a tutorial from: https://www.guguweb.com/2022/01/23/django-rest-framework-authentication-the-easy-way/

from rest_framework import serializers
from api.models import User
from django.core.validators import validate_email
from django.core.exceptions import ValidationError


class RegisterSerializer(serializers.Serializer):
    username = serializers.CharField(
        label="Username",
        write_only=True
    )
    password = serializers.CharField(
        label="Password",
        style={'input_type': 'password'},
        trim_whitespace=False,
        write_only=True
    )
    firstname = serializers.CharField(
        label="Firstname",
        write_only=True
    )
    surname = serializers.CharField(
        label="Surname",
        write_only=True
    )

    def validate(self, request):
        # Get username and password from request
        username = request.get("username", None)
        password = request.get("password", None)
        firstname = request.get("firstname", None)
        surname = request.get("surname", None)

        if username and password and firstname and surname:
            if User.objects.filter(username=username).exists() is True:
                # User already exists
                raise serializers.ValidationError("A user with that email already exists", code='email-in-use')
            
            # TODO: more validators
            try:
                validate_email(username)
            except ValidationError:
                raise serializers.ValidationError("Please enter a valid email address", code='invalid-email')
            
            User.objects.create_user(username=username,
                                     password=password,
                                     email=username,
                                     first_name=firstname,
                                     last_name=surname)
            return True
        else:
            raise serializers.ValidationError("You need to enter all fields.", code='authorization')