from django.db import models
from django.contrib.auth.models import AbstractUser
from rest_framework.authtoken.models import Token

# Extensible user account with custom parameters
class User(AbstractUser):
    def save(self, *args, **kwargs):
        super(User, self).save(*args, **kwargs)
        # We want a token for every created user
        Token.objects.get_or_create(user=self)
            
    def __str__(self):
        return self.username