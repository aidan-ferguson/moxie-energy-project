from django.test import TestCase
from django import urls
import json
from rest_framework.authtoken.models import Token
from django.test import Client, TestCase
from api.models import User
from rest_framework.test import 

TEST_USERNAME = 'testuser@test.com'
TEST_PASSWORD = "test_password"
TEST_FIRSTNAME = "Test"
TEST_LASTNAME = "User"
test_user = User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
token = Token.objects.get_or_create(user=test_user)[0]
TEST_CLIENT = Client(HTTP_AUTHORIZATION=f"Token {token}")

class TestConnection(TestCase):
    def test_connection_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("test-connection"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_connection_invalid_token(self):
        response = self.client.get(urls.reverse("test-connection"), HTTP_AUTHORIZATION=f"Token invalid")
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Invalid token.")
        
    def test_connection_authenticated_success(self):
        response = TEST_CLIENT.get(urls.reverse("test-connection"))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content)["data"]["message"], f"Hello, {test_user.username}")

class RegisterTest(TestCase):
    def test_register_preexisting_user(self):
        response = TEST_CLIENT.post(urls.reverse("register"), **{"username":TEST_USERNAME, "password":TEST_PASSWORD, "firstname":TEST_FIRSTNAME, "surname":TEST_LASTNAME})
        print(response.content)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content)["non_field_errors"], {})