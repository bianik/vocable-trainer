# Report
## The program
The for the project chosen program is a vocable trainer, which can be used for learning vocables in another language by repetitively checking the vocables. The vocables are added to the application providing a phrase in the language to learn and it's translation in the mother tongue. After adding the vocables they can be learned by calling a vocable's translation and entering the corresponding phrase. The given answer will be checked with the phrase of the earlier added vocable and the result will be shown providing information about whether the phrase was correct or incorrect along with the number of correct tries, overall tries and a percentile ratio. After that the next vocable can be checked provided in the order the vocables have been added earlier, starting and also continuing with the first one after checking the last vocable. A new vocable can be added at any time.  
The application can be run with a command line interface or a graphical user interface, depending on an argument given on start up along with some other arguments for internal settings.  

Console user interface:  
<img src="./console_screenshot.jpeg" width="500">  
Graphical user interface:  
<img src="./gui_screenshot1.jpeg" height="230"> -> <img src="./gui_screenshot2.jpeg" height="230">  ->
<img src="./gui_screenshot3.jpeg" height="230"> -> <img src="./gui_screenshot4.jpeg" height="230">  

## Implementation
### Design and implementation choices
The application has been written in the programming language Java using the model-view-presenter architecture implemented with the following classes:

- The **model** is implemented by the class [Vocable](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/model/Vocable.java) storing the phrase, it's translation and the number of correct and incorrect tries. The vocable-objects are stored in a database implemented in the wrapper class [H2VocableRepository](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/repository/h2/H2VocableRepository.java) using the 3rd party library [h2 database](http://www.h2database.com) providing a server-based or in-memory SQL-database.
- The **presenter** implemented by the [TrainerController](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/controller/TrainerController.java) class is responsible for the program's logic by coordinating the view and the model.  
While adding a [newVocable](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/controller/TrainerController.java#L20), the controller checks, whether that vocable already exists in the repository. (As a design choice, the phrase has been chosen to be the unique identifier of the vocables.) While learning the controller looks for the vocable in the repository, then [check(s the)VocableOnGivenPhrase](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/controller/TrainerController.java#L33) and updates the number of tries in the repository. It also provides the view with the [nextVocable](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/controller/TrainerController.java#L57) following the current vocable.
- The **view** is implemented by the class [ConsoleTrainerView](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/view/console/ConsoleTrainerView.java) providing the console ui and the class [SwingTrainerView](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/view/swing/SwingTrainerView.java) providing the GUI using the Java GUI-toolkit swing.

Beside these classes the class [VocableTrainerApp](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/app/VocableTrainerApp.java), containing the main-method is, called on start up and connects all the collaborators and starts the view. It also  configures the application using the following optional command line arguments:

- `--console`,`-c` use console interface instead of GUI
- `--h2-user` h2 DB username
-	`--h2-password` h2 DB password
-	`--h2-table`,`-t` h2 DB table name
- `--h2-port`,`-p` h2 DB TCP port
-	`--h2-host` h2 DB TCP port
-	`--h2-in-memory`,`-m` h2 DB use in-memory DB
-	`--h2-no-init` h2 DB do not initialize

The command line arguments are being parsed with the library [picocli](https://picocli.info).

### Development and Testing
The application has been written using test-driven development and the testing frameworks **JUnit 4**, **AssertJ (Swing)**, **Awaitility** and **Mockito**.
#### Unit Tests
Starting with [unit tests](myProjects.vocableTrainer/src/test) the overall specifications of the application have been set and implemented beginning with the class TrainerController, then continuing with the H2VocableRepository, the SwingTrainerView and the ConsoleTrainerView.

For the class Vocable no testing has been used since this is a plain domain model containing mostly fields and getters and setters. Beside these and two functions for incrementing the number of tries, the class has two separate equals-methods. One of which is the normal autogenerated [equals](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/model/Vocable.java#L80)-method comparing all the classes fields and is used for testing. The other method [compareTo](myProjects.vocableTrainer/src/main/java/myProjects/vocableTrainer/model/Vocable.java#L64) is used to compare only the phrase and translation of tow vocable-objects ignoring the number of tries and is used by the SUT. The reason for the second method will be further explained in the Problems section.  
Also the class VocableTrainerApp has been excluded from unit tests since this class only connects the collaborators and starts the application so it has been written after all the collaborators have been fully implemented and will be tested with e2e tests.

For unit testing the wrapper class H2VocableRepository an in-memory database has been used for speeding up the tests and so to say mocking a "real" database, which would have to be started and stopped every time.

In every single unit test case all the other collaborators have been mocked to allow independent development and testing. The only exception is again the domain class Vocable, which real implementation is used but spied on in order to verify calls of it's methods.

#### Integration Tests
In the integration tests the individually developed components are tested by interconnecting them and verifying the intended behavior focussing only on the main functionalities leaving out every possible path.  
Starting with [H2VocableRepositoryIT](myProjects.vocableTrainer/src/it/java/myProjects/vocableTrainer/repository/h2/H2VocableRepositoryIT.java) this time a server-based database running in a Docker container is used. The container is build based on a [Dockerfile](myProjects.vocableTrainer/Dockerfile), which sets up the h2 database and it's ports, starts before integration testing and stops afterwards.  
The [TrainerControllerIT](myProjects.vocableTrainer/src/it/java/myProjects/vocableTrainer/controller/TrainerControllerIT.java) tests the integration between the TrainerController and the H2VocableRepository while still mocking the view. This allows for concentrating on these two parts without worrying to much about the view's behavior.  
Next the [SwingTrainerViewIT](myProjects.vocableTrainer/src/it/java/myProjects/vocableTrainer/view/swing/SwingTrainerViewIT.java) and [ConsoleTrainerViewIT](myProjects.vocableTrainer/src/it/java/myProjects/vocableTrainer/view/console/ConsoleTrainerViewIT.java) are written testing the integration of all components with the command line UI or GUI. The assertions on this class are being executed by interacting with UI while still using package-private interactions with the class for setting up the test fixture.

#### End-to-End Tests
Finally the whole application is tested in two test cases, one for each UI, interacting with the SUT only via the UI. The test fixture is set up with one already existing Vocable in the repository, this time added manually not reusing the wrapper class which is also part of the SUT. In [SwingTrainerViewE2E](myProjects.vocableTrainer/src/e2e/java/myProjects/vocableTrainer/app/VocableTrainerAppSwingE2E.java) the GUI is tested starting the application from the command line with some command line arguments.  
The [ConsoleTrainerViewE2E](myProjects.vocableTrainer/src/e2e/java/myProjects/vocableTrainer/app/VocableTrainerAppConsoleE2E.java) does not start the application from the command line but rather creates an instance of the VocableTrainerApp and runs it in a new thread. This needs to be done in order to get the input and output and interact with the UI. Having two running threads the test asserts the application's output by waiting for the SUT to finish the task and return an output using **Awaitility**.

### Applied techniques and frameworks

#### SonarCloud disabled rules
-
-

### Problems
#### Testing the console line interface
#### spying on the Vocable class
#### Database password
