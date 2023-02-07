# Django REST framework seralisers
# Based on a tutorial from: https://www.guguweb.com/2022/01/23/django-rest-framework-authentication-the-easy-way/

from django.contrib.auth import authenticate
from rest_framework import serializers

class LoginSerializer(serializers.Serializer):
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

    def validate(self, request):
        # Get username and password from request
        username = request.get('username')
        password = request.get('password')

        if username and password:
            # Try to authenticate the user using Django auth framework.
            user = authenticate(request=self.context.get('request'),
                                username=username, password=password)
            if user:
                if user.is_active:
                    # Now there is a valid user return to the view
                    request['user'] = user
                    return request
                else:
                    raise serializers.ValidationError("Sorry, your account has been de-activated, please contact the administrator for assistance.", code='authorization')
            else:
                # If we don't have a regular user, raise a ValidationError
                raise serializers.ValidationError("Wrong username or password.", code='authorization')
        else:
            raise serializers.ValidationError("Sorry, looks like you didn't enter a username and password!", code='authorization')