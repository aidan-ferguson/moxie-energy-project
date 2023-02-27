from django.test import TestCase
from django import urls
import json

class TestConnection(TestCase):
    def test_connection_unauthenticated_failure(self):
        response = self.client.get(urls.reverse('test-connection'))
        self.assertEqual(response.status_code, 400)
        self.assertEqual(json.loads(response.content)["detail"], "Authentication credentials were not provided.")
