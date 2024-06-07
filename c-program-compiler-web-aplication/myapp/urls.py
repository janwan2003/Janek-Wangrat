from django.contrib import admin
from django.urls import include, path
from django.views.generic import RedirectView
from . import views

app_name = 'myapp'
urlpatterns = [
    path('admin/', admin.site.urls),
    path('home', views.index, name='index'),
    path("register", views.register_request, name="register"),
    path("login", views.login_request, name="login"),
    path("logout", views.logout_request, name= "logout"),
    path('add_catalog', views.add_catalog, name='add_catalog'),
    path('add_file', views.add_file, name='add_file'),
    path('delete_item', views.delete_item, name='delete_item'),
    path('display_file', views.display_file, name='display_file'),
    path('compile', views.compile, name='compile'),
    path('download_asm', views.download_asm, name='download_asm'),
    path('', RedirectView.as_view(url='/home/')),
    path('edit_file', views.edit_file, name='edit_file'),
]