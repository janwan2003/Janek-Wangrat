from django.contrib.auth.models import User
from django.db import models
from django.utils import timezone



class Catalog(models.Model):
    name = models.CharField(max_length=255, unique=True)
    description = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    is_deleted = models.BooleanField(default=False)
    deleted_at = models.DateTimeField(null=True, blank=True)
    last_content_change = models.DateTimeField(null=True, blank=True)
    parent_catalog = models.ForeignKey('self', null=True, blank=True, on_delete=models.CASCADE)

    def delete(self):
        self.is_deleted = True
        self.deleted_at = timezone.now()
        self.save()

    def __str__(self):
        return self.name


class File(models.Model):
    name = models.CharField(max_length=255, unique=True)
    file = models.FileField(upload_to='uploads/', blank=True, null=True)
    description = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    is_deleted = models.BooleanField(default=False)
    deleted_at = models.DateTimeField(null=True, blank=True)
    last_content_change = models.DateTimeField(null=True, blank=True)
    parent_catalog = models.ForeignKey(Catalog, on_delete=models.CASCADE, blank=True, null=True)

    def delete(self):
        self.is_deleted = True
        self.deleted_at = timezone.now()
        self.save()

    def __str__(self):
        return self.name


class FileSection(models.Model):
    name = models.CharField(max_length=255, blank=True, null=True, unique=True)
    description = models.TextField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True) 
    start_line = models.IntegerField()
    end_line = models.IntegerField()
    section_type = models.ForeignKey('SectionType', on_delete=models.CASCADE)
    status = models.ForeignKey('SectionStatus', on_delete=models.CASCADE)
    status_data = models.TextField(blank=True, null=True)
    content = models.TextField()

    def __str__(self):
        return self.name


class SectionType(models.Model):
    name = models.CharField(max_length=255, unique=True)

    def __str__(self):
        return self.name


class SectionStatus(models.Model):
    name = models.CharField(max_length=255, unique=True)

    def __str__(self):
        return self.name
