from django.shortcuts import render, redirect, get_object_or_404
from .models import Catalog, File
from django.contrib.auth import login, authenticate, logout
from django.views.decorators.http import require_POST
from django.contrib import messages
from .forms import *
from django.contrib.auth.forms import AuthenticationForm
import subprocess, argparse, os
from django.http import FileResponse, JsonResponse
from django.conf import settings
import json
import re
from django.core.files.base import ContentFile

def register_request(request):
	if request.method == "POST":
		form = NewUserForm(request.POST)
		if form.is_valid():
			user = form.save()
			login(request, user)
			messages.success(request, "Registration successful." )
			return redirect("myapp:index")
		messages.error(request, "Unsuccessful registration. Invalid information.")
	form = NewUserForm()
	return render (request=request, template_name="myapp/register.html", context={"register_form":form})

def login_request(request):
	if request.method == "POST":
		form = AuthenticationForm(request, data=request.POST)
		if form.is_valid():
			username = form.cleaned_data.get('username')
			password = form.cleaned_data.get('password')
			user = authenticate(username=username, password=password)
			if user is not None:
				login(request, user)
				messages.info(request, f"You are now logged in as {username}.")
				return redirect("myapp:index")
			else:
				messages.error(request,"Invalid username or password.")
		else:
			messages.error(request,"Invalid username or password.")
	form = AuthenticationForm()
	return render(request=request, template_name="myapp/login.html", context={"login_form":form})

def logout_request(request):
	logout(request)
	messages.info(request, "You have successfully logged out.") 
	return redirect("myapp:index")

def index(request):
    # Fetch the required data from your database
    catalogs = Catalog.objects.all()
    files = File.objects.all()

    # Check if file_name exists in the session and retrieve corresponding File object
    file_name = request.session.get('file_name')

    displayed_file = None
    asm_code = None
    asm_err = None
    sections = []
    row_number = None
    if file_name:
        displayed_file = get_object_or_404(File, name=file_name)
        # Pass the data to the template context

        file_path = displayed_file.file.path
        asm_folder = os.path.join(os.path.dirname(file_path), 'asm')  # Create an 'asm' folder in the same directory as the source file
        if not os.path.exists(asm_folder):
            os.mkdir(asm_folder)
        asm_file_name = os.path.basename(file_path).replace('.c', '.asm')
        asm_file_path = os.path.join(asm_folder, asm_file_name)  # Set the path to the assembly file in the 'asm' folder
        asm_err_path = os.path.join(asm_folder, asm_file_name.replace('.asm', '.err'))
        if os.path.exists(asm_file_path):
            with open(asm_file_path, 'r') as f:
                asm_code = f.read()
        else:
            asm_code = None
        
        if os.path.exists(asm_err_path):
            with open(asm_err_path, 'r') as f:
                asm_err = f.read()
            match = re.search(r'\.c:(\d+)', asm_err)
            if match:
                row_number = match.group(1)
        else:
            asm_err = None
        sections = []
        header_type, _ = SectionType.objects.get_or_create(name="Header")
        text_type, _ = SectionType.objects.get_or_create(name="Text")
        if asm_code:
            asm_split = asm_code.split(';--------------------------------------------------------')
            for i, section_text in enumerate(asm_split):
                section = FileSection()
                section.name = f"Section {i}"
                section.description = ""
                section.section_type = header_type if i % 2 == 0 else text_type
                section.content = section_text.strip()  # Set the section content, removing any leading/trailing whitespace
                # Add the new section object to the list
                sections.append(section)
    if displayed_file:
        displayed_file_text = displayed_file.file.open().read().decode().strip()
    else:
        displayed_file_text = None
        
    context = {
        'catalogs': catalogs,
        'files': files,
        'displayed_file_text': displayed_file_text if displayed_file_text else "Choose a file from the list!",
        'displayed_file' : displayed_file if displayed_file else None,
        'asm_code' : asm_code if asm_code else None,
        'asm_err' : asm_err if asm_err else None,
        'sections' : sections if sections else None,
        'row_number' : row_number if row_number else None,
    }

    # Render the index template with the required data
    return render(request, 'myapp/index.html', context)


def add_catalog(request):
    if request.method == 'POST':
        form = CatalogForm(request.POST)
        if form.is_valid():
            catalog = form.save(commit=False)
            catalog.owner = request.user
            catalog.save()
            messages.success(request, f"You've successfully added the file '{catalog.name}'.")
    else:
        form = CatalogForm()
    return render(request, 'myapp/add_catalog.html', {'form': form})

def add_file(request):
    if request.method == 'POST':
        form = FileForm(request.POST, request.FILES)
        if form.is_valid():
            file = form.save(commit=False)
            file.owner = request.user
            file.save()
            messages.success(request, f"You've successfully added the file '{file.name}'.")
    else:
        form = FileForm()
    return render(request, 'myapp/add_file.html', {'form': form})

def edit_file(request):
    if request.method == 'POST':
        file_name = request.session.get('file_name')
        file_text = request.POST.get('file_text')
        try:
            file = File.objects.get(name=file_name)
            with file.file.open(mode='w') as f:
                f.write(file_text)
            file.last_content_change = timezone.now()
            file.save()
            return JsonResponse({'success': True, 'message': 'File saved successfully.'})
        except File.DoesNotExist:
            return JsonResponse({'success': False, 'message': 'File not found.'})
    else:
        return JsonResponse({'success': False, 'message': 'Invalid request method.'})

def delete_item(request):
    if request.method == 'POST':
        form = DeleteForm(request.POST)
        if form.is_valid():
            catalog = form.cleaned_data['catalog']
            file = form.cleaned_data['file']
            if catalog:
                catalog.delete()
                messages.success(request, f"You've successfully deleted the catalog '{catalog.name}'.")
            elif file:
                file.delete()
                messages.success(request, f"You've successfully deleted the file '{file.name}'.")
    else:
        form = DeleteForm()
    return render(request, 'myapp/delete_item.html', {'form': form})

def display_file(request):
    file_name = request.GET['file_name']
    request.session['file_name'] = file_name
    displayed_file = get_object_or_404(File, name=file_name)
    
    text = displayed_file.file.open().read().decode().strip()
    
    response_data = {
        'success': True,
        'message': text,
        # Include any additional session data you want to send back
    }

    return JsonResponse(response_data)


def compile(request):
    file_name = request.session.get('file_name')
    our_file = get_object_or_404(File, name=file_name)

    standard = request.POST.get('standard')
    optimizations = request.POST.getlist('optimizations[]')  # Use getlist instead of get
    processor = request.POST.get('processor')
    dependencies = request.POST.get('dependencies')

    # Compile the file using the selected options
    SDCC_FLAGS = []

    if standard:
        SDCC_FLAGS += ['--std-' + standard]
    
    if optimizations:
        for opt in optimizations:
            SDCC_FLAGS += ['--' + opt]  # Add each optimization as a separate flag
    if processor:
        SDCC_FLAGS += ['-m' + processor]  # Use processor value as a flag
    
    if dependencies:
        SDCC_FLAGS += ['--' + dependencies]
    
    # Compile the file using sdcc
    file_path = our_file.file.path
    asm_folder = os.path.join(os.path.dirname(file_path), 'asm')  # Create an 'asm' folder in the same directory as the source file
    if not os.path.exists(asm_folder):
        os.mkdir(asm_folder)
    asm_file_name = os.path.basename(file_path).replace('.c', '.asm')
    asm_file_path = os.path.join(asm_folder, asm_file_name)  # Set the path to the assembly file in the 'asm' folder
    asm_err_path = os.path.join(asm_folder, asm_file_name.replace('.asm', '.err'))

    if os.path.exists(asm_file_path):
        os.remove(asm_file_path)
    
    command = ["sdcc"] + SDCC_FLAGS + ["-S", "-o", asm_file_path, file_path]

    with open(asm_err_path, 'w') as err_file:
        subprocess.run(command, stderr=err_file)  # Use the '-o' flag to specify the output file
    #print(command)
    # Read and display the assembly code
    return redirect('myapp:index')

def download_asm(request):
    file_name = request.session.get('file_name')
    our_file = get_object_or_404(File, name=file_name)
    file_path = our_file.file.path
    asm_folder = os.path.join(os.path.dirname(file_path), 'asm')
    asm_file_name = os.path.basename(file_path).replace('.c', '.asm')
    asm_file_path = os.path.join(asm_folder, asm_file_name)

    with open(asm_file_path, 'r') as f:
        file_data = f.read()

    response = FileResponse(file_data, as_attachment=True)
    response['Content-Type'] = 'application/octet-stream' # Set the content type header
    response['Content-Disposition'] = 'inline; filename=' + asm_file_name
    return response


