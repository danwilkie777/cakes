Overview
--------

- The app has been implemented using Coroutines and Flow in combination with MVVM
- Most of my recent projects used RxJava in combination with MVP or MVI, so this was a relatively new approach for me

Modules
-------

- Since the app is a small demo app with one screen, everything is in the app module
- I would typically separate out feature modules, along with a common module and the app module
- These can optionally be further split into layers for domain, ui, network, platform
- The package structure gives an indication of this structure

What could be done with more time?
-----------------------------------------

- The data could be saved to disk using Room. This would give offline support on fresh launches. Instead, for now it is cached in memory with a SharedFlow
- For larger apps, with non-trivial business logic, a use-case can be added between the ViewModel and the Repository, particularly useful if e.g. we need to combine data from multiple sources
- The build.gradle could be tidied up with some version bumping and more variables declared for versions
- The tests for the RetrofitCakeListService could read JSON from separate files
- The LCE (Loading, Content, Error) states might fit better in the ViewModel instead of the Repository

