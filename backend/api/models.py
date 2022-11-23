from django.db import models
from django.contrib.auth.models import AbstractUser

# Extensible user account with custom parameters
class User(AbstractUser):    
    def save(self, *args, **kwargs):
        super(User, self).save(*args, **kwargs)    
            
    def __str__(self):
        return self.username