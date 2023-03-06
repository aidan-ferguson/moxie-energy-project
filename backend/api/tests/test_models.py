from django.test import TestCase
from api import models
from rest_framework.authtoken.models import Token
import datetime

TEST_USERNAME = 'testuser@test.com'
TEST_PASSWORD = "test_password"
TEST_FIRSTNAME = "Test"
TEST_LASTNAME = "User"

# Unit tests for the TestConnection view
class TestUser(TestCase):
    def test_create(self):
        user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
        self.assertTrue(user is not None)
        
    def test_save_creates_token(self):
        user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
        user.save()
        self.assertTrue(models.Token.objects.filter(user=user).count() == 1)
        
    def test_user_to_str(self):
        user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
        self.assertTrue(user.__str__() == TEST_USERNAME)
        
class TestTip(TestCase):
    def test_create_auto_data(self):
        user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
        tip = models.Tip.objects.get_or_create(user=user, device="test_device", text="test_text")[0]
        self.assertEqual(tip.date, datetime.date.today())
        
    def test_tip_to_str(self):
        user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
        tip = models.Tip.objects.get_or_create(user=user, device="test_device", text="test_text")[0]
        self.assertTrue(tip.__str__() == f"{tip.date} - {tip.text}")
        
