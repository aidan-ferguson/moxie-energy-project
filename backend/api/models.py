from django.contrib.auth.models import AbstractUser
from rest_framework.authtoken.models import Token
from django.db import models
from api.utils import get_user_energy_data, calculate_energy_score


# Extensible user account with custom parameters
class User(AbstractUser):
    # Will indicate which data source the user wants to use, can be mock data or an API
    data_provider = models.TextField(name="data_provider", default="DALE:house_4")
    
    def save(self, *args, **kwargs):
        super(User, self).save(*args, **kwargs)
        # We want a token for every created user
        Token.objects.get_or_create(user=self)
            
    def delete(self, *args, **kwargs):
        Token.objects.filter(user=self).delete()
        super(User, self).delete(*args, **kwargs)
        
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


# For holding friendships between users
class Friendship(models.Model):
    from_user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="from_user")
    to_user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="to_user")
    has_accepted = models.BooleanField(default=False)
    
    def __str__(self):
        return f"{self.from_user.email} -> {self.to_user.email} : accepted = {self.has_accepted}"
    
    # Get a list of all accepted friends of the current user
    @staticmethod
    def get_user_friends(user):
        friends = list(Friendship.objects.filter(from_user=user, has_accepted=True))
        friends += list(Friendship.objects.filter(to_user=user, has_accepted=True))
        
        return friends
    
    # Return a list of unaccepted incoming friend requests
    @staticmethod
    def get_friend_requests(user):
        return list(Friendship.objects.filter(to_user=user, has_accepted=False))

    # Get the details of the user on the other end of the frienship than the current user
    @staticmethod
    def friendship_to_json(friendship, user, is_request=True):
        user_to_seralise = friendship.to_user if user == friendship.from_user else friendship.from_user
        ret_val = {'id': user_to_seralise.id, 'firstname': user_to_seralise.first_name, 'surname': user_to_seralise.last_name}
        if not is_request:
            ret_val['score'] = calculate_energy_score(get_user_energy_data(user_to_seralise)["data"])
        return ret_val
