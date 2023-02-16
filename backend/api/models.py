from django.contrib.auth.models import AbstractUser
from rest_framework.authtoken.models import Token
from django.db import models



# Extensible user account with custom parameters
class User(AbstractUser):
    # Will indicate which data source the user wants to use, can be mock data or an API
    data_provider = models.TextField(name="data_provider", default="DALE:house_4")
    
    def save(self, *args, **kwargs):
        super(User, self).save(*args, **kwargs)
        # We want a token for every created user
        Token.objects.get_or_create(user=self)
            
    def __str__(self):
        return self.username

# Model for caching any tips
class Tip(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True)
    device = models.TextField(name="device")
    date = models.DateField(auto_now=True)
    text = models.TextField()
    
    def __str__(self):
        return f"{self.date} - {self.text}"
    
# Model for caching the global tip of the day
class TOTD(models.Model):
    date = models.DateField(auto_now=True)
    tip = models.ForeignKey(Tip, on_delete=models.CASCADE)
    
    def __str__(self):
        return self.tip.__str__()
        