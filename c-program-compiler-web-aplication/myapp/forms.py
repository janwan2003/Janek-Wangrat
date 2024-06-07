from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import *

class CatalogForm(forms.ModelForm):
    parent_catalog = forms.ModelChoiceField(queryset=Catalog.objects.all(), required=False)

    class Meta:
        model = Catalog
        fields = ['name', 'description', 'parent_catalog']
	
class FileForm(forms.ModelForm):
    file = forms.FileField(required=True)
    name = forms.CharField(max_length=255, required=True)
    parent_catalog = forms.ModelChoiceField(queryset=Catalog.objects.all(), required=False)

    class Meta:
        model = File
        fields = ['name', 'description', 'parent_catalog', 'file']

    def save(self, commit=True):
        file = super().save(commit=False)
        file.name = self.cleaned_data['name']
        file.file = self.cleaned_data['file']
        if commit:
            file.save()
        return file

class NewUserForm(UserCreationForm):
	email = forms.EmailField(required=True)

	class Meta:
		model = User
		fields = ("username", "password1", "password2")

	def save(self, commit=True):
		user = super(NewUserForm, self).save(commit=False)
		user.email = self.cleaned_data['email']
		if commit:
			user.save()
		return user
        
class DeleteForm(forms.Form):
    catalog = forms.ModelChoiceField(
        queryset=Catalog.objects.filter(is_deleted=False), 
        required=False)
    file = forms.ModelChoiceField(
        queryset=File.objects.filter(is_deleted=False), 
        required=False)