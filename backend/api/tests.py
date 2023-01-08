from django.test import TestCase
from django import urls
import json

class TestConnection(TestCase):
    def test_connection_success(self):
        response = self.client.get(urls.reverse('test_connection'))
        self.assertEqual(response.status_code, 200)
        self.assertTrue(json.loads(response.content)["success"], "Connection to server failed.")