from django.urls import path
from . import views

urlpatterns = [
    path('test_connection', views.test_connection, name='test_connection'),
    path('users/<user_id>/usage/electricity', views.electricity_usage, name='electricity_usage')
]