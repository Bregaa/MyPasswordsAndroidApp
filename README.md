# MyPasswords
**MyPasswords is an Android app that helps you generate, retrieve, and manage passwords without storing any password or private data.**

## Introduction - How does it work?
![How does it work](images/readme//how_does_it_work.png)
Each password is created by [hashing](#what-is-hashing-and-how-are-the-generated-passwords-safe) the combination of the profile's email, the service you need a password for, and a secret key you choose.
For example, "email@email.com", "google" and "mypassword" will always generate the password "hq5Pf2I27s+Ty1i" (try it yourself in the app!).

The app locally stores only a hash of your email (to avoid saving your email), a hash of your secret key ([Why is this saved?](#why-is-a-hash-of-the-secret-key-saved)) and a list of services you've created passwords for, never your private data. This allows you to recreate your passwords anytime by selecting your profile, choosing the service, and entering your secret key (shared by all services on that profile).

**The hash mechanism ensures the same password is generated every time the same three parameters are used. Different emails, services or secret keys will generate different passwords.** This means that only you are able to re-generate your passwords (because only you know the secret key), and nothing actually needs to be stored ([Why is the email hash's and the service list saved?](#why-is-the-email-hashs-and-the-service-list-saved)).

To better understand how the app works, read the following [features](#features-and-screenshots) and [F.A.Q](#faq) sections.

## Features and screenshots

The following screenshots are made using a default theme. The app uses google's "Material You" to customize automatically the app's colors based on your phone's material theme. A dark theme will also automatically be applied when the system's dark theme is enabled.

### Multiple profiles to separate different emails passwords

| ![Image 1](images/readme/app%20screenshots/1.jpg) | ![Image 2](images/readme/app%20screenshots/2.jpg) | ![Image 3](images/readme/app%20screenshots/3.jpg) |
|------------------------|-----------------------|------------------------|
| ![Image 4](images/readme/app%20screenshots/4.jpg) | ![Image 10](images/readme/app%20screenshots/10.jpg) | ![Image 11](images/readme/app%20screenshots/11.jpg) |

In the app you can set up different profiles, to manage passwords across different emails. **By having multiple profiles the passwords generated will be different, even if you're using the same exact service names and secret key!**

If you choose a name for the profile the email won't be saved, otherwise it will be used as the name.

### Generate a new password

| ![Image 5](images/readme/app%20screenshots/5.jpg) | ![Image 7](images/readme/app%20screenshots/7.jpg) |
|------------------------|-----------------------|
| ![Image 8](images/readme/app%20screenshots/8.jpg) | ![Image 9](images/readme/app%20screenshots/9.jpg) |

To create a new password, you must input the service's name (e.g. google, instagram) and your secret key. All passwords in a profile must use the same secret key, but you can use different ones across multiple profiles if you wish.

The genrated password can then be shown, hidden or copied to clipboard, and aliases can be added if you wish ([how do I manage aliases?](#manage-a-services-aliases)).

### Retrieve a password

| ![Image 13](images/readme/app%20screenshots/13.jpg) | ![Image 15](images/readme/app%20screenshots/15.jpg) |
|------------------------|-----------------------|

Through the "search" page, you're able to quickly find services that you've already generated a password for. The research looks at both service names and their aliases (if you've set them).

This feature avoids misspelling the service name (e.g. google -> gogle) since that would generate a completely different password. It also helps you find the correct service name through the aliases (e.g. you're logging in into youtube, but the password is the same as google and gmail, but you don't know which one of the three names you've used to generate the password).

### Manage a service's aliases
| ![Image 14](images/readme/app%20screenshots/14.jpg) | ![Image 20](images/readme/app%20screenshots/20.jpg) |
|------------------------|-----------------------|

Aliases ([What are aliases?](#what-are-services-aliases)) can be added or removed after generating or retrieving a password by pressing on the "aliases" button. Aliases must not have been already used (as service name or as alias) in the same profile.

### Homepage (recent passwords)

| ![Image 5](images/readme/app%20screenshots/5.jpg) | ![Image 12](images/readme/app%20screenshots/12.jpg) |
|------------------------|-----------------------|

The homepage has a list of the last 10 password you've created or retrieved for quick access. You can press the items to open their retrieve page.

### Profiles settings

| ![Image 1](images/readme/app%20screenshots/16.jpg) | ![Image 2](images/readme/app%20screenshots/17.jpg) |
|------------------------|-----------------------|
 ![Image 2](images/readme/app%20screenshots/18.jpg) | ![Image 2](images/readme/app%20screenshots/19.jpg) |

In settings page (opened from the settings button in the side drawer menu), for each profile, you can edit the displayed name, you can check if you remember the password correctly and you can delete the profile.


## F.A.Q.
### What is "hashing?" and how are the generated passwords safe?
Hashing is a cryptographic process used to convert input data into a fixed-size string of characters. This string, called a hash, is unique to the input (different inputs generating the same hash are possible but extremely rare and negligible in this case) and it cannot be used to reverse the process and find the initial input.

In MyPassword, the passwords are generated by hashing the combination of your email's hash ([not directly the email since it's not saved](#why-is-the-email-hashs-and-the-service-list-saved)), the service you're creating the password for (e.g. google, instagram) and your secret key.

This process is safe because it's not invertable: even if a data leak happens in one of the websites you've used, that password cannot be used to find the 3 parameters that generated it. Also, a different password is generated for each service, because of the different parameter used for the service's name (remember: the hash is unique to the combination of the input parameters).

### Why is a hash of the secret key saved?
A hash of the secret key is saved locally when a new profile is added on the app. 

Every time a password has to be created or retrieved, the secret key is asked so it can be used as the third parameter to generate the password. A hash of the input secret key is then calculated and matched with the saved hash, to check if the input is correct. This way, the app will make sure you haven't misspelled the secret key and that the generated password is correct, without having to store any private data. Since hashes are not invertable saving this data is safe and worth it, to avoid generating incorrect passwords because of a typo.

### Why is the email hash's and the service list saved?
A hash of the **profile's email** is saved when a new profile is created on the app. This allows you to create different profiles for different emails, that will generate differnt passwords for the same services, even if you use the same secret key! The saved hash if necessary to divide the informations about the different profiles you've created (secret key's hash and list of services), without saving your email in plain text.

Also, to avoid having to input your email every time a password has to be generated, the hash is used directly  as the email parameter in the calculation.

A **list of services** is saved for each profile, to remember which services you've already generated a password for and to simplify their retrieval. This, together with the [aliases mechanism](), gets rid of the issue of remembering what service name you used for each service, and how did you spell it (since you need an exact spelling to re-generate the same password). 

### What are service's aliases?
Service aliases are other ways you could call the service you've created a password for.

For example, if you've generated a password for your google account, using "google" as service name, you might later not remember if you've used "google", "gmail" or "youtube". The app saves "google" as the primary service name, and lets you add "gmail" and "youtube" as aliases that you can use in the search page to select and retrieve "google"'s password.

### Does the app save or collect any sensitive information?

The app saves locally on your device (in protected folders only accessible by the app) the secret key's hash ([why is this saved?](#why-is-a-hash-of-the-secret-key-saved)), the email's hashes and the list of services you've created a password for ([why are these saved?](#why-is-the-email-hashs-and-the-service-list-saved)).

No data at all is collected, shared or will in any way leave your device.

### What is the future of this app?

This android app is only the first step of my project. I plan to start working on a **windows app** soon that can basically do the same job, while sharing the necessary data in a secure and private manner.

### Can I use this app right now?
Yes! Simply download the apk from the latest release and install it on your phone! Otherwise feel free to clone the whole repository and compile it yourself. 

The app is free to use, you are allowed to use it personally and share it as-is, but your are NOT allowed to make use of it commercially. Any use intended to make money off of my work or to steal it is strictly forbidden. Any modification to this source code must include a credit to my work, this paragraph and must not be used commercially.

### Will the app be mantained and remain free?
Yes. I plan to use this myself as soon as the whole project is completed, so bugs will be fixed and requested features will be taken into consideration. The app is free to use and contains no ads, and I don't have any plan to change that at the moment. Once the project is completed I might consider publishing it on the google play store.