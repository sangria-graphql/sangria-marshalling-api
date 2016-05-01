## v0.2.1 (2016-05-01)

* Introduced `ScalarValueInfo` and `MarshallerCapability` marker traits to allow scalar values to coerce output values based on the marshaller capabilities.
* Added a set of standard marshaller capabilities that may be supported by concrete implementations natively: `DateSupport`, `CalendarSupport` and `BlobSupport`.   
* `ResultMarshaller` now only has 2 methods for scalar value marshalling: `scalarNode` and `enumNode`. They get much more info about the value as well.

## v0.2.0 (2016-03-24)

* Introduced map builder which is able to preserve the field order and provides much faster way to build a 
  map (uses mutable data structures to minimize memory footprint)

## v0.1.1 (2016-01-28)

* Added `InputParser` type class which may be implemented by marshalling library in order to provide parsing from string feature 
  (required for default value support in schema materialization)

## v0.1.0 (2016-01-23)

* Initial release 