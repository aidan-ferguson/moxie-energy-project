from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token
from . import views

urlpatterns = [
    path('test-connection', views.TestView.as_view(), name='test-connection'),
    path('auth/register', views.RegisterView.as_view(), name='login'),
    path('auth/logout', views.logout_user, name='logout'),
    path('usage/appliances', views.get_appliances, name='get_appliances'),
    path('usage/tips', views.get_tips, name="get_tips"),
    path('usage/national-average', views.NationalAverage.as_view(), name='national-average'),
    path('auth/get-token', obtain_auth_token, name='token_auth'),
    path('user/information', views.UserInfoView.as_view(), name='user-information')
]
