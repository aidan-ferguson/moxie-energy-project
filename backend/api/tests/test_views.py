from django.test import TestCase
from django import urls
import json
from rest_framework.authtoken.models import Token
from api import models
from rest_framework.test import APIClient
import os

TEST_USERNAME = 'testuser@test.com'
TEST_PASSWORD = "test_password"
TEST_FIRSTNAME = "Test"
TEST_LASTNAME = "User"
test_user = models.User.objects.get_or_create(username=TEST_USERNAME, password=TEST_PASSWORD, email=TEST_USERNAME, first_name=TEST_FIRSTNAME, last_name=TEST_LASTNAME,  data_provider="DALE:house_4")[0]
token = Token.objects.get_or_create(user=test_user)[0]
TEST_CLIENT = APIClient(HTTP_AUTHORIZATION=f"Token {token}")

# Unit tests for the TestConnection view
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

# Unit tests for the RegisterView (note don't need to check for missing inputs as they are handled by DjangoRestFramework)
class RegisterTest(TestCase):
    def test_register_preexisting_user(self):
        data = {"username":TEST_USERNAME, "password":TEST_PASSWORD, "firstname":TEST_FIRSTNAME, "surname":TEST_LASTNAME}
        response = self.client.post(urls.reverse("register"), data, format="json")
        self.assertEqual(response.status_code, 400)
        self.assertEqual(json.loads(response.content)["non_field_errors"][0], "A user with that email already exists")
        
    def test_register_invalid_email(self):
        data = {"username":"invalidemail", "password":TEST_PASSWORD, "firstname":TEST_FIRSTNAME, "surname":TEST_LASTNAME}
        response = self.client.post(urls.reverse("register"), data, format="json")
        self.assertEqual(response.status_code, 400)
        self.assertEqual(json.loads(response.content)["non_field_errors"][0], "Please enter a valid email address")
        
    def test_register_success(self):
        data = {"username": "test2@test.com", "password":TEST_PASSWORD, "firstname":TEST_FIRSTNAME, "surname":TEST_LASTNAME}
        response = self.client.post(urls.reverse("register"), data, format="json")
        self.assertEqual(response.status_code, 202)
    
# For testing the user info view    
class UserInfoTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("user-information"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
    
    def test_get_correct_info(self):
        response = TEST_CLIENT.get(urls.reverse("user-information"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], True)
        self.assertEqual(json_response["data"]["username"], TEST_USERNAME)
        self.assertEqual(json_response["data"]["firstname"], TEST_FIRSTNAME)
        self.assertEqual(json_response["data"]["surname"], TEST_LASTNAME)
        self.assertTrue(isinstance(json_response["data"]["energy_score"], float))
        self.assertTrue(json_response["data"]["energy_score"] >= 0 and json_response["data"]["energy_score"] <= 1)
        
    def test_update_info(self):
        data = {"action":"update", "firstname":"NewFirstName", "last_name":"NewLastName", "data_provider":"DALE:house_2"}
        response = TEST_CLIENT.post(urls.reverse("user-information"), data, format="json")
        self.assertEqual(response.status_code, 200)
        response = TEST_CLIENT.get(urls.reverse("user-information"))
        json_response = json.loads(response.content)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json_response["success"], True)
        self.assertEqual(json_response["data"]["firstname"], "NewFirstName")
        self.assertEqual(json_response["data"]["surname"], "NewLastName")
        self.assertEqual(json_response["data"]["data_provider"], "DALE:house_2")
        # Change the data provider back to the original
        data = {"action": "update", "data_provider":"DALE:house_4"}
        response = TEST_CLIENT.post(urls.reverse("user-information"), data, format="json")
        self.assertEqual(response.status_code, 200)
        
    def test_cant_update_username(self):
        data = {"action":"update", "username":"NewUsername"}
        response = TEST_CLIENT.post(urls.reverse("user-information"), data, format="json")
        self.assertEqual(response.status_code, 200)
        response = TEST_CLIENT.get(urls.reverse("user-information"))
        json_response = json.loads(response.content)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json_response["success"], True)
        self.assertEqual(json_response["data"]["username"], TEST_USERNAME)
        
    def test_no_update_action_error(self):
        response = TEST_CLIENT.post(urls.reverse("user-information"), {}, format="json")
        self.assertEqual(response.status_code, 400)

# For testing the national avergae view
class NationalAverageTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("national-average"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_is_floats(self):
        response = TEST_CLIENT.get(urls.reverse("national-average"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], True)
        for key in json_response["data"]:
            self.assertTrue(isinstance(json_response["data"][key], float))
            
            
# For testing the tip of the day view
class TOTDTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("tip-of-the-day"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_tip_generation(self):
        response = TEST_CLIENT.get(urls.reverse("tip-of-the-day"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], True)
        self.assertTrue(isinstance(json_response["data"], str))

    # This should also implicitly test that the tip has been cached
    def test_global_tip(self):
        second_user = models.User.objects.get_or_create(username="test2@test.com", password="test", email="test2@test.com", first_name="Test", last_name="McTest",  data_provider="DALE:house_4")[0]
        second_token = Token.objects.get_or_create(user=second_user)[0]
        second_client = APIClient(HTTP_AUTHORIZATION=f"Token {second_token}")
        
        response = TEST_CLIENT.get(urls.reverse("tip-of-the-day"))
        json_response = json.loads(response.content)
        first_tip = json_response["data"]
        
        response = second_client.get(urls.reverse("tip-of-the-day"))
        json_response = json.loads(response.content)
        second_tip = json_response["data"]
        
        self.assertEqual(first_tip, second_tip)
        
    def test_no_openai_key_failure(self):
        api_key  = os.environ.get("OPENAI_API_KEY")
        del os.environ["OPENAI_API_KEY"]
        response = TEST_CLIENT.get(urls.reverse("tip-of-the-day"))
        self.assertEqual(response.status_code, 500)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], False)
        self.assertEqual(json_response["reason"], "An internal error occured with generating tips")
        os.environ["OPENAI_API_KEY"] = api_key
        
class EnergyReportTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("energy-report"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_tip_generation(self):
        response = TEST_CLIENT.get(urls.reverse("energy-report"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], True)
        self.assertTrue(isinstance(json_response["data"], str))

    def test_tip_caching(self):
        response = TEST_CLIENT.get(urls.reverse("energy-report"))
        json_response = json.loads(response.content)
        tip = json_response["data"]
        cached_tip = models.Tip.objects.filter(device="aggregate", user=test_user)
        self.assertTrue(cached_tip.exists())
        self.assertEqual(tip, cached_tip.first().text)
        
    def test_no_openai_key_failure(self):
        api_key  = os.environ.get("OPENAI_API_KEY")
        del os.environ["OPENAI_API_KEY"]
        response = TEST_CLIENT.get(urls.reverse("tip-of-the-day"))
        self.assertEqual(response.status_code, 500)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], False)
        self.assertEqual(json_response["reason"], "An internal error occured with generating tips")
        os.environ["OPENAI_API_KEY"] = api_key

# Test the appliance tips view
class ApplianceTipsTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("appliance-tips"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_tip_generation(self):
        response = TEST_CLIENT.get(urls.reverse("energy-report"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], True)
        self.assertTrue(isinstance(json_response["data"], str))

    def test_tip_caching(self):
        response = TEST_CLIENT.get(urls.reverse("energy-report"))
        json_response = json.loads(response.content)
        tip = json_response["data"]
        cached_tip = models.Tip.objects.filter(device="aggregate", user=test_user)
        self.assertTrue(cached_tip.exists())
        self.assertEqual(tip, cached_tip.first().text)
        
    def test_different_appliance_different_tip(self):
        # Device names came from the house_4 dataset
        response = TEST_CLIENT.get(urls.reverse("appliance-tips"), {"device": "kettle_radio"})
        json_response = json.loads(response.content)
        first_tip = json_response["data"]
        
        response = TEST_CLIENT.get(urls.reverse("appliance-tips"), {"device": "freezer"})
        json_response = json.loads(response.content)
        second_tip = json_response["data"]
        
        self.assertNotEqual(first_tip, second_tip)
        
    def test_no_openai_key_failure(self):
        api_key  = os.environ.get("OPENAI_API_KEY")
        del os.environ["OPENAI_API_KEY"]
        response = TEST_CLIENT.get(urls.reverse("tip-of-the-day"))
        self.assertEqual(response.status_code, 500)
        json_response = json.loads(response.content)
        self.assertEqual(json_response["success"], False)
        self.assertEqual(json_response["reason"], "An internal error occured with generating tips")
        os.environ["OPENAI_API_KEY"] = api_key

# For testing the appliance view
class ApplianceViewest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("get-appliances"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
    
    def test_response_contains_all_fields(self):
        response = TEST_CLIENT.get(urls.reverse("get-appliances"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertTrue("labels" in json_response["data"])
        self.assertTrue("initial_usage" in json_response["data"])
        self.assertTrue("previous_week" in json_response["data"])
        self.assertTrue("today" in json_response["data"])
        self.assertTrue("energy_score" in json_response["data"])
        
    def test_parallel_arrays(self):
        response = TEST_CLIENT.get(urls.reverse("get-appliances"))
        json_response = json.loads(response.content)
        self.assertTrue((
            len(json_response["data"]["labels"]) == len(json_response["data"]["initial_usage"]) and
            len(json_response["data"]["labels"]) == len(json_response["data"]["previous_week"]) and
            len(json_response["data"]["labels"]) == len(json_response["data"]["today"])
        ))

# Test that logging out deletes the token
class LogoutTest(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("logout"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
    
    def test_logout_delete_token(self):
        second_user = models.User.objects.get_or_create(username="test3@test.com", password="test", email="test3@test.com", first_name="Test", last_name="McTest",  data_provider="DALE:house_4")[0]
        second_token = Token.objects.get_or_create(user=second_user)[0]
        second_client = APIClient(HTTP_AUTHORIZATION=f"Token {second_token}")
        second_client.get(urls.reverse("logout"))
        tokens = Token.objects.filter(user=second_user)
        self.assertEqual(len(tokens), 1)
        
class TestFriends(TestCase):
    second_user = models.User.objects.get_or_create(username="test4@test.com", password="test", email="test4@test.com", first_name="Test", last_name="McTest",  data_provider="DALE:house_4")[0]
    second_token = Token.objects.get_or_create(user=second_user)[0]
    second_client = APIClient(HTTP_AUTHORIZATION=f"Token {second_token}")
    
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("friends"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_get_friends(self):
        response = TEST_CLIENT.get(urls.reverse("friends"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertTrue(isinstance(json_response["data"]["friends"], list))
        self.assertTrue(isinstance(json_response["data"]["requests"], list))
        
    def test_user_id_nonint_failure(self):
        data = {"user_id": "not an int"}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 400)
        json_response = json.loads(response.content)
        self.assertFalse(json_response["success"])
        self.assertEqual(json_response["reason"], "User ID must be an integer")
        
    def test_no_action_failure(self):
        data = {"user_id": 1}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 400)
        json_response = json.loads(response.content)
        self.assertFalse(json_response["success"])
        self.assertEqual(json_response["reason"], "You need to send a user id and an action")
        
    def test_user_id_doesnt_exist_failure(self):
        data = {"user_id": 100000, "action": "make_request"}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 404)
        json_response = json.loads(response.content)
        self.assertFalse(json_response["success"])
        self.assertEqual(json_response["reason"], "Could not find a user with that ID")
        
    def test_invalid_action_failure(self):
        data = {"action": "not an action", "user_id": 1}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 400)
        json_response = json.loads(response.content)
        self.assertFalse(json_response["success"])
        self.assertEqual(json_response["reason"], "You must send a valid action")

    def test_make_deny_request(self):
        # Make initial request
        data = {"action": "make_request", "user_id": self.second_user.id}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertEqual(json_response["data"], "Added request")
        
        # Check we can't make the same request again
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 400)
        self.assertEqual(json.loads(response.content)["reason"], "You have already sent a friend request")
        
        # Check the other user can see the request
        response = self.second_client.get(urls.reverse("friends"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertEqual(len(json_response["data"]["requests"]), 1)
        self.assertEqual(json_response["data"]["requests"][0]["id"], test_user.id)
        
        # Test denying the request
        data = {"action": "deny_request", "user_id": test_user.id}
        response = self.second_client.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 200)
        
        self.assertFalse(models.Friendship.objects.filter(from_user=test_user, to_user=self.second_user).exists())
        
    def test_make_accept_request(self):
        # Make initial request
        data = {"action": "make_request", "user_id": self.second_user.id}
        response = TEST_CLIENT.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertEqual(json_response["data"], "Added request")
        
        # Test accepting the request
        data = {"action": "accept_request", "user_id": test_user.id}
        response = self.second_client.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 200)
        
        # Test we can see the request
        response = TEST_CLIENT.get(urls.reverse("friends"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertEqual(json_response["data"]["friends"][0]["id"], self.second_user.id)
        
    def test_accept_nonexistant_request(self):
        data = {"action": "accept_request", "user_id": test_user.id}
        response = self.second_client.post(urls.reverse("friends"), data, format="json")
        self.assertEqual(response.status_code, 400)
        self.assertEqual(json.loads(response.content)["reason"], "That friend request does not exist")
        
class TestDataProviderView(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("list-data-providers"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_correct_sources_listed(self):
        response = TEST_CLIENT.get(urls.reverse("list-data-providers"))
        self.assertEqual(response.status_code, 200)
        json_response = json.loads(response.content)
        self.assertTrue(json_response["success"])
        self.assertTrue(isinstance(json_response["data"], list))
        for data_provider in json_response["data"]:
            self.assertTrue(data_provider.startswith("DALE"))
    
class TestDeleteAccountView(TestCase):
    def test_unauthenticated_failure(self):
        response = self.client.get(urls.reverse("delete-account"))
        self.assertEqual(response.status_code, 401)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
        
    def test_delete_account(self):
        second_user = models.User.objects.get_or_create(username="test5@test.com", password="test", email="test5@test.com", first_name="Test", last_name="McTest",  data_provider="DALE:house_4")[0]
        second_token = Token.objects.get_or_create(user=second_user)[0]
        second_client = APIClient(HTTP_AUTHORIZATION=f"Token {second_token}")
        response = second_client.post(urls.reverse("delete-account"))
        self.assertEqual(response.status_code, 200) 
        self.assertFalse(models.User.objects.filter(username="test5@test.com").exists())