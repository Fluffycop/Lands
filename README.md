# Lands
A land claiming plugin

Lands is a land claiming plugin that also comes with a very simple towns system. I've written it as a way to demonstrate my programming ability, but I do plan on working on it more in the future to develop it into something production-worthy

# Features
As of right now, Lands features a very basic land claiming system where leaders of towns can claim any and however many chunks they want in any world. Players can either be the leader of a town or a member and can only be in one town at a time

# Implementation
The basic idea is that every system involving towns is separated into its own manager object, which handles it own internals and exposes a nice API to use. Managers should not depend on other managers, to avoid problems with the order of loading in data from persistent sources. The TownManager object is a little special in this regard, as it is the "source of truth" when it comes to the validity of towns. (ie: Are they still real or disbanded?) Other managers can depend on the TownManager's API, and as a result the TownManager is always loaded first.

Every manager defines its own persistence mechanisms through implementing the `Persistent<T>` interface, where the generic defines the type of the datastructure that will be saved. In JSON's case this is just a more Gson friendly representation of the data. 
SQL has not been implemented, but if or when I do get to that, I'm planning on passing a datastructure that defines the changes between the last time a save was called and the current time. Sort of like "smart" event sourcing.

# Final Words
Obviously there's still a lot of stuff that needs to be added to make this thing actually usable. I mainly wrote it up because I need to show that I can maintain good quality even while writing (relatively) complicated functionality. If you're planning on reading the code, I'd suggest getting familiar with each of abstract manager classes and reading how the town commands work. When you want to see how JSON persistence is implemented, view all the classes that start with `Json`. The `PersistenceManager` is the class that ties together all the persistence mechanisms. It handles all the serialization, deserialization, and I/O.
