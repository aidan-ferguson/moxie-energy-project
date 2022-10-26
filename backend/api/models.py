from django.db import models
from django.contrib.auth.models import AbstractUser

class User(AbstractUser):
    def save(self, *args, **kwargs):
        # Custome attributes get saved here
        super(User, self).save(*args, **kwargs)    
            
    def __str__(self):
        return self.username