
## C Program Web Compiler - Web Application

1. Install dependencies from `pyproject.toml` - I recommend using Poetry:
   ```sh
   poetry install
   ```

2. Install `sdcc` by running the following command:
   ```sh
   # Command to install sdcc (provide specific command or instructions here)
   ```

3. Run the application using:
   ```sh
   python manage.py runserver
   ```

---

### Description

The C Program Web Compiler is a web application designed to facilitate the compilation of C programs through a user-friendly interface. Once dependencies are installed and the application is running, users can interact with the platform to compile their C with some nice debugs and features.

Users can log in to the application and manage their files and directories. The application's interface allows users to upload C source files, which are then stored in a structured directory system within the database.

The application supports real-time interactions without the need for page reloads. Users can manually define sections of their code, and the application provides detailed feedback on compilation errors directly within the interface. This feedback includes line-specific error messages and allows users to highlight the corresponding lines in their source code for easier debugging.
Users can also view and manage the assembly code generated from their C source files, with options to collapse and expand different sections as needed.