# Overview
## The short version
Charon aims to be to Java, what Automapper is to .NET
## The slightly longer version
This library aims at helping you to easily transform one object into another object. This behaviour is especially desired in applications that receive data from a web service or a message queue. The objects that come from these are usually autogenerated and are often clumsy to work with and lack any behaviour. Applications that have behaviour-rich objects would rather not tolerate these anemic objects. This is where Charon comes into play. Using this library one could express transformation rules that would allow to extract the data from the data-transfer objects and construct a domain object based on this data.
# The features
 * Transformation rules expressed in code
 * XML-free
# The advantages
 * Flexible expressive mapping of fields between objects
 * Auto-mapping of fields based on their name (upcoming feature)
 * Renaming fields and classes automatically reflects on the mapping rules
# The benefits
 * Removes some "boring" code
 * Less or no maintainance for the mapping code due to refactoring (depends on the refactoring)
 * Allows for generating reports about the way the data flows between different parts of your system
# The name
For the long version you should google "Charon", but here is the short version - In greek mythology Charon is the ferryman who carries the souls of the newly deceased from the world of the living to the underworld