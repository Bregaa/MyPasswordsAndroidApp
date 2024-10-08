# MyPasswords
**MyPasswords is an Android app that helps you generate, retrieve, and manage passwords without storing any password or private data.**

## Introduction - How does it work?
![How does it work](images/readme//how_does_it_work.png)
Each password is created by [hashing](#what-is-hashing-and-how-are-the-generated-passwords-safe) the combination of the profile's email, the service name (e.g., "google"), and a secret key of your choice.
For instance, if you use "email@email.com," "google," and "mypassword," the app will consistently generate the same password (try it yourself in the app!). Changing any parameter even slightly will result in a completely different password.

The app stores locally only a hash of your email (to avoid storing the actual email), a hash of your secret key ([Why is this saved?](#why-is-a-hash-of-the-secret-key-saved)) and a list of services you've created passwords for. This allows you to recreate your passwords anytime by selecting your profile, choosing the service, and entering your secret key (shared across all services on that profile).

**The hash mechanism ensures the same password is generated every time the same three parameters are used. Different emails, service names or secret keys will generate different passwords.** This means that only you can regenerate your passwords, as only you know the secret key, and nothing actually needs to be stored ([Why are the email hash's and the service list saved?](#why-are-the-email-hashs-and-the-service-list-saved)).

For more details, check out the [Features](#features-and-screenshots) and [F.A.Q](#faq) sections below.

## Features and screenshots

The following screenshots showcase the app using the default theme. The app uses google's "Material You", automatically adapting its colors to match your phone's theme. If your system's dark theme is enabled, the app will automatically switch to a dark theme as well.

### Multiple profiles to separate different emails passwords

| ![Image 1](images/readme/app%20screenshots/1.jpg) | ![Image 2](images/readme/app%20screenshots/2.jpg) | ![Image 3](images/readme/app%20screenshots/3.jpg) |
|------------------------|-----------------------|------------------------|
| ![Image 4](images/readme/app%20screenshots/4.jpg) | ![Image 10](images/readme/app%20screenshots/10.jpg) | ![Image 11](images/readme/app%20screenshots/11.jpg) |

The app allows you to create multiple profiles, allowing you to manage passwords across different emails. **With multiple profiles, even if you use the same service names and secret key, the generated passwords will be different for each email!**

You can name profiles to avoid saving your email; otherwise, the email will be used as the profile name.

### Generate a new password

| ![Image 5](images/readme/app%20screenshots/5.jpg) | ![Image 7](images/readme/app%20screenshots/7.jpg) | ![Image 8](images/readme/app%20screenshots/8.jpg) | ![Image 9](images/readme/app%20screenshots/9.jpg) |
|------------------------|-----------------------|-----------------------|-----------------------|

To generate a new password, simply enter the service's name (e.g., "google" or "instagram") and your secret key. Each profile requires a consistent secret key for all its passwords, but you can use different keys across different profiles if desired.

Genrated passwords can then be shown, hidden or copied to clipboard, and aliases can be added if you wish ([learn more about aliases](#manage-a-services-aliases)).

### Retrieve a password

| ![Image 13](images/readme/app%20screenshots/13.jpg) | ![Image 15](images/readme/app%20screenshots/15.jpg) | ![Image 21](images/readme/app%20screenshots/21.jpg) |
|------------------------|-----------------------|-----------------------|

The "search" page allows you to quickly find services for which you've already generated passwords. The search function considers both service names and any aliases you've set up.

This feature helps prevent misspellings (e.g., "google" vs. "gogle"), which would generate a different password. It also helps you find the correct service name through the aliases (for example, you might not remember if you used "google," "gmail," or "youtube" for your password).

### Manage a service's aliases
| ![Image 14](images/readme/app%20screenshots/14.jpg) | ![Image 20](images/readme/app%20screenshots/20.jpg) | ![Image 22](images/readme/app%20screenshots/22.jpg) |
|------------------------|-----------------------|-----------------------|

Aliases ([What are aliases?](#what-are-services-aliases)) can be added or removed after generating or retrieving a password by pressing the "aliases" button. Note that aliases must be unique within the same profile and cannot be reused as service names or other aliases.

### Homepage (recent passwords)

| ![Image 5](images/readme/app%20screenshots/5.jpg) | ![Image 12](images/readme/app%20screenshots/12.jpg) | ![Image 23](images/readme/app%20screenshots/23.jpg) |
|------------------------|-----------------------|-----------------------|

The homepage displays a list of the last 10 passwords you've created or retrieved for quick access. Tapping on an item will take you directly to its retrieval page.

### Profiles settings

| ![Image 16](images/readme/app%20screenshots/16.jpg) | ![Image 17](images/readme/app%20screenshots/17.jpg) | ![Image 18](images/readme/app%20screenshots/18.jpg) | ![Image 19](images/readme/app%20screenshots/19.jpg) |
|------------------------|-----------------------|------------------------|-----------------------|

In the settings page (accessible via the settings button in the side drawer menu), you can manage each profile by editing its displayed name, checking if you remember its password correctly and deleting the profile.


## F.A.Q.
### What is "hashing?" and how are the generated passwords safe?
Hashing is a cryptographic process that converts input data into a fixed-size string of characters, known as a hash. This hash is unique to the input (different inputs generating the same hash are possible but extremely rare and negligible in this case). **Importantly, hashing is a one-way process, meaning it’s not possible to reverse-engineer the original input from the hash.**

In MyPassword, the passwords are generated by hashing the combination of your email's hash ([not directly the email since it's not saved](#why-is-the-email-hashs-and-the-service-list-saved)), the service you're creating the password for (e.g. "google", "instagram") and your secret key. This ensures that each password is unique to the specific combination of these inputs.

The process is secure because it’s non-reversible: even if a password were compromised, it couldn’t be used to discover the original parameters. Different passwords are generated for each service due to the unique service name parameter.

### Why is a hash of the secret key saved?
When you create a new profile in the app, a hash of your secret key is saved locally. 

Whenever you generate or retrieve a password, you'll be prompted to enter the secret key. The app hashes this input and compares it with the stored hash to ensure accuracy. This process helps prevent typos and ensures the correct password is generated, all without storing any sensitive data. Since hashes cannot be reversed, storing them is safe and effective for maintaining accuracy.

### Why are the email hash's and the service list saved?
When you create a new profile, a hash of the profile's **email** is saved locally. This allows you to have separate profiles for different emails, ensuring that even with the same secret key, the passwords generated for the same services will be different. Storing the hash if essential for distinguishing between profiles and organizing associated data (like the secret key's hash and service list) without storing your email in plain text. The hash is also used directly in the password generation process, eliminating the need to re-enter your email each time.

Also, to avoid having to input your email every time a password has to be generated, the hash is used directly  as the email parameter in the calculation.

A **list of services** ist saved for each profile, to remember which services you've already generated a password for and to simplify their rerieval. This, combined with the [aliases feature](#what-are-services-aliases), helps you remember service names and ensures correct password generation.

### What are service's aliases?
Service aliases are alternative names you can use to refer to a service  you’ve created a password for.

For example, if you've generated a password for your Google account using "google" as service name, you might later not remember if you've used "google", "gmail" or "youtube". The app saves "google" as the primary service name, and lets you add "gmail" and "youtube" as aliases that you can use in the search page to find and retrieve "google"'s password.

### Does the app save or collect any sensitive information?

The app saves locally on your device (in protected folders only accessible by the app) the secret key's hash ([why is this saved?](#why-is-a-hash-of-the-secret-key-saved)), the email's hashes and the list of services you've created a password for ([why are these saved?](#why-is-the-email-hashs-and-the-service-list-saved)).

No data at all is collected, shared or will in any way leave your device.

### What is the future of this app?

This Android app is just the beginning of the project. I plan to develop a **Windows app** next, which will offer the same functionality while securely and privately sharing necessary data across platforms.

For developers: yes, the mechanism I've used to store the necessary data locally is terrible to manage (but still safe). And yes, it will be changed in a future version, once the android app and the windows app will have to share this data. Why did I do it that way? Because lots of features like aliases and multiple profiles were not planned at the beginning, so since I didn't have to store much, that system was good enough.

### Can I use this app right now?
**Yes!** Simply download the apk from the latest release and install it on your phone! Otherwise feel free to clone the whole repository and compile it yourself. 

The app is free to use, you are allowed to use it personally and share it as-is, but commercial use is prohibited. Any use intended to make money off of my work is strictly forbidden. Any modifications to the source code must include credit to the original author and must not be used commercially.

### Will the app be mantained and remain free?
**Yes!** I plan to use this app myself as soon as the whole project is completed, so bugs will be fixed and requested features will be taken into consideration.

The app is free to use, contains no ads, and I have no plans to change that. Once the project is complete, I may consider publishing it on the Google Play Store for more people to enjoy.