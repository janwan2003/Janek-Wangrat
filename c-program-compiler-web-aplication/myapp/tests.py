from django.contrib.auth.models import User
from django.test import TestCase, Client
from django.utils import timezone
from .models import *
from .forms import *
from django.urls import reverse
from django.contrib.auth.models import User
from django.contrib.messages import get_messages
from django.core.files.uploadedfile import SimpleUploadedFile
import os


class CatalogModelTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='Vuh4I9dO5Pfs4S!')
        self.catalog = Catalog.objects.create(name='Test Catalog', owner=self.user)

    def test_delete(self):
        self.catalog.delete()
        self.assertTrue(self.catalog.is_deleted)
        self.assertIsNotNone(self.catalog.deleted_at)

    def test_str(self):
        self.assertEqual(str(self.catalog), 'Test Catalog')


class FileModelTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='Vuh4I9dO5Pfs4S!')
        self.catalog = Catalog.objects.create(name='Test Catalog', owner=self.user)
        self.file = File.objects.create(name='Test File', owner=self.user, parent_catalog=self.catalog)

    def test_delete(self):
        self.file.delete()
        self.assertTrue(self.file.is_deleted)
        self.assertIsNotNone(self.file.deleted_at)

    def test_str(self):
        self.assertEqual(str(self.file), 'Test File')


class FileSectionModelTest(TestCase):
    def setUp(self):
        self.section_type = SectionType.objects.create(name='Test Section Type')
        self.section_status = SectionStatus.objects.create(name='Test Section Status')
        self.file_section = FileSection.objects.create(name='Test File Section',
                                                       start_line=1,
                                                       end_line=10,
                                                       section_type=self.section_type,
                                                       status=self.section_status,
                                                       content='Test content')

    def test_str(self):
        self.assertEqual(str(self.file_section), 'Test File Section')


class SectionTypeModelTest(TestCase):
    def setUp(self):
        self.section_type = SectionType.objects.create(name='Test Section Type')

    def test_str(self):
        self.assertEqual(str(self.section_type), 'Test Section Type')


class SectionStatusModelTest(TestCase):
    def setUp(self):
        self.section_status = SectionStatus.objects.create(name='Test Section Status')

    def test_str(self):
        self.assertEqual(str(self.section_status), 'Test Section Status')


class CatalogFormTest(TestCase):
    def test_valid_form(self):
        form_data = {
            'name': 'Test Catalog',
            'description': 'Test description',
            'parent_catalog': None,
        }
        form = CatalogForm(data=form_data)
        self.assertTrue(form.is_valid())

    def test_invalid_form(self):
        form_data = {
            'name': '',
            'description': 'Test description',
            'parent_catalog': None,
        }
        form = CatalogForm(data=form_data)
        self.assertFalse(form.is_valid())


class FileFormTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='Vuh4I9dO5Pfs4S!')
        self.catalog = Catalog.objects.create(name='Test Catalog', owner=self.user)

    def test_valid_form(self):
        file_content = b'This is a test file.'
        file_name = 'testfile.txt'
        file_data = SimpleUploadedFile(file_name, file_content)
        
        form_data = {
            'name': 'Test File',
            'description': 'Test description',
            'parent_catalog': self.catalog.id,
            'file': file_data,
        }
        form = FileForm(data=form_data, files=form_data)
        self.assertTrue(form.is_valid())

    def test_invalid_form(self):
        form_data = {
            'name': '',
            'description': 'Test description',
            'parent_catalog': self.catalog.id,
        }
        form = FileForm(data=form_data)
        self.assertFalse(form.is_valid())


class NewUserFormTest(TestCase):
    def test_valid_form(self):
        form_data = {
            'username': 'testuser',
            'email': 'test@example.com',
            'password1': 'Vuh4I9dO5Pfs4S!',
            'password2': 'Vuh4I9dO5Pfs4S!',
        }
        form = NewUserForm(data=form_data)
        self.assertTrue(form.is_valid())

    def test_invalid_form(self):
        form_data = {
            'username': 'testuser',
            'email': '',
            'password1': 'Vuh4I9dO5Pfs4S!',
            'password2': 'Vuh4I9dO5Pfs4S!',
        }
        form = NewUserForm(data=form_data)
        self.assertFalse(form.is_valid())


class DeleteFormTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='Vuh4I9dO5Pfs4S!')
        self.catalog = Catalog.objects.create(name='Test Catalog', owner=self.user)
        self.file = File.objects.create(name='Test File', parent_catalog=self.catalog, owner=self.user)

    def test_valid_form(self):
        form_data = {
            'catalog': self.catalog.id,
            'file': self.file.id,
        }
        form = DeleteForm(data=form_data)
        self.assertTrue(form.is_valid())

class ViewTests(TestCase):
    def setUp(self):
        self.client = Client()
        self.user = User.objects.create_user(username='testuser', password='Vuh4I9dO5Pfs4S!', email='testemail@gmail.com')
        self.catalog = Catalog.objects.create(name='Test Catalog', owner=self.user)
        
        # Create a temporary file to use for testing
        file_content = b'This is a test file.'
        file_name = 'testfile.txt'
        file_data = SimpleUploadedFile(file_name, file_content)
        
        # Create a file object with the uploaded file
        self.file = File.objects.create(name='testfile', owner=self.user, file=file_data)
                
        self.login_url = reverse('myapp:login')
        self.logout_url = reverse('myapp:logout')

    def test_register_request_POST_valid_form(self):
        response = self.client.post(reverse('myapp:register'), {
            'username': 'NewUser',
            'email': 'studentpiwo@gmail.com',
            'password1': 'StudentPiwo1',
            'password2': 'StudentPiwo1',
        })
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, reverse('myapp:index'))
        self.assertEqual(User.objects.count(), 2)
        self.assertEqual(User.objects.last().username, 'NewUser')

    def test_register_request_POST_invalid_form(self):
        response = self.client.post(reverse('myapp:register'), {
            'username': 'NewUser',
            'email': 'NewUser',
            'password1': 'NewUser',
            'password2': 'NewUser',
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(User.objects.count(), 1)

    def test_login_POST_valid_credentials(self):
        response = self.client.post(self.login_url, {
            'username': 'testuser',
            'password': 'Vuh4I9dO5Pfs4S!',
        })
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, reverse('myapp:index'))
        self.assertEqual(str(response.wsgi_request.user), 'testuser')
        messages = list(get_messages(response.wsgi_request))
        self.assertEqual(len(messages), 1)
        self.assertEqual(str(messages[0]), "You are now logged in as testuser.")

    def test_login_POST_invalid_credentials(self):
        response = self.client.post(self.login_url, {
            'username': 'testuser',
            'password': 'wrongpassword',
        })
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/login.html')
        messages = list(get_messages(response.wsgi_request))
        self.assertEqual(len(messages), 1)
        self.assertEqual(str(messages[0]), "Invalid username or password.")

    def test_login_GET(self):
        response = self.client.get(self.login_url)
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/login.html')
        form = response.context['login_form']
        self.assertIsNotNone(form)
        self.assertFalse(form.is_bound)

    def test_logout_request(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:logout'))
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, reverse('myapp:index'))
        self.assertFalse('_auth_user_id' in self.client.session)

    def test_add_catalog_POST_valid_form(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.post(reverse('myapp:add_catalog'), {
            'name': 'New Catalog',
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(Catalog.objects.count(), 2)
        self.assertEqual(Catalog.objects.last().name, 'New Catalog')

    def test_add_catalog_POST_invalid_form(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.post(reverse('myapp:add_catalog'), {
            'name': '',
        })
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/add_catalog.html')
        self.assertEqual(Catalog.objects.count(), 1)

    def test_add_file_POST_valid_form(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        file_data = b'Test file data'
        file = SimpleUploadedFile('testfile.txt', file_data)
        response = self.client.post(reverse('myapp:add_file'), {
            'name': 'New File',
            'file': file,
        })
        self.assertEqual(response.status_code, 200)  # Redirects to index page
        self.assertEqual(File.objects.count(), 2)
        self.assertEqual(File.objects.last().name, 'New File')

    def test_add_file_POST_invalid_form(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.post(reverse('myapp:add_file'), {
            'name': '',
            'file': 'testfile.txt',
        })
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/add_file.html')
        self.assertEqual(File.objects.count(), 1)

    def test_delete_item_POST_delete_catalog(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.post(reverse('myapp:delete_item'), {
            'catalog': self.catalog.name,
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(Catalog.objects.count(), 1)

    def test_delete_item_POST_delete_file(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        
        response = self.client.post(reverse('myapp:delete_item'), {
            'file': self.file.name,
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(File.objects.count(), 1)


    def test_display_file(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:display_file'), {
            'file_name': self.file.name,
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()['success'], True)
        self.assertEqual(response.json()['message'], 'This is a test file.')

    def test_compile(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:display_file'), {
            'file_name': self.file.name,
        })
        response = self.client.post(reverse('myapp:compile'), {
            'standard': 'c11',
            'processor': 'z80',
        })
        self.assertEqual(response.status_code, 302)
        self.assertEqual(response.url, reverse('myapp:index'))

    def test_download_asm(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:display_file'), {
            'file_name': self.file.name,
        })
        response = self.client.post(reverse('myapp:compile'), {
            'standard': 'c11',
            'processor': 'z80',
        })
        response = self.client.get(reverse('myapp:download_asm'))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.get('Content-Type'), 'application/octet-stream')
    
    def test_index(self):
        response = self.client.get(reverse('myapp:index'))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/index.html')

    def test_register_request_GET(self):
        response = self.client.get(reverse('myapp:register'))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/register.html')

    def test_login_request_GET(self):
        response = self.client.get(reverse('myapp:login'))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/login.html')

    def test_logout_request_GET(self):
        response = self.client.get(reverse('myapp:logout'))
        self.assertEqual(response.status_code, 302)
        self.assertRedirects(response, reverse('myapp:index'))

    def test_add_file_GET(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:add_file'))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/add_file.html')

    def test_display_file_invalid_file(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.get(reverse('myapp:display_file'), {
            'file_name': 'nonexistent.txt',
        })
        self.assertEqual(response.status_code, 404)

    def test_compile_invalid_file(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        response = self.client.post(reverse('myapp:compile'), {
            'standard': 'c11',
            'processor': 'z80',
        })
        self.assertEqual(response.status_code, 404)

    def test_download_asm_invalid_file(self):
        self.client.login(username='testuser', password='Vuh4I9dO5Pfs4S!')
        self.client.session['file_name'] = 'nonexistent.txt'
        response = self.client.get(reverse('myapp:download_asm'))
        self.assertEqual(response.status_code, 404)

    def test_index_no_file_name_in_session(self):
        response = self.client.get(reverse('myapp:index'))
        
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/index.html')
        
        # Assert that the context variables are set correctly
        self.assertContains(response, "Choose a file from the list!")
        self.assertIsNone(response.context['displayed_file'])
        self.assertIsNone(response.context['asm_code'])
        self.assertIsNone(response.context['asm_err'])
        self.assertIsNone(response.context['sections'])
        self.assertIsNone(response.context['row_number'])
        
    def test_index_file_name_in_session_with_asm_file(self):
        file_name = 'testfile'
        self.client.session['file_name'] = file_name
        self.client.session.save()
        
        # Create the asm file in the specified folder
        file_path = self.file.file.path
        asm_folder = os.path.join(os.path.dirname(file_path), 'asm')
        os.makedirs(asm_folder, exist_ok=True)
        asm_file_path = os.path.join(asm_folder, 'testfile.asm')
        with open(asm_file_path, 'w') as f:
            f.write('; Sample ASM Code')
        
        response = self.client.get(reverse('myapp:index'))
        
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/index.html')
        
    def test_index_file_name_in_session_with_no_asm_file(self):
        file_name = 'testfile'
        self.client.session['file_name'] = file_name
        self.client.session.save()
        
        response = self.client.get(reverse('myapp:index'))
        
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'myapp/index.html')