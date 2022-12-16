from django.urls import path
from . import views

urlpatterns = [
    path('test_connection', views.test_connection, name='test_connection'),
    path('auth/csrf_token', views.get_csrf_token, name='get_csrf_token'),
    path('auth/login', views.login_user, name='login'),
    path('auth/logout', views.logout_user, name='logout'),
    path('usage/electricity', views.electricity_usage, name='electricity_usage'),
    path('usage/appliances', views.get_appliances, name='get_appliances'),
    path('usage/tips', views.get_tips, name="get_tips"),
]