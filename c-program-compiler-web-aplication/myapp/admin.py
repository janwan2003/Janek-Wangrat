from django.contrib import admin
from .models import Catalog, File, FileSection, SectionType, SectionStatus, User

# Register your models here.

admin.site.register(Catalog)
admin.site.register(File)
admin.site.register(FileSection)
admin.site.register(SectionType)
admin.site.register(SectionStatus)
