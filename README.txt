@Exported annotation means that class can be serialized and deserialized.
@PropertyName annotation defines a special name of the field in serialization.
@Ignored annotation tells mapper to skip a field while serialization or deserialization.
@DateTime annotation defines a special format for Date and Time.

Before the name of the fields itself the internal path of class id stored. It is done to handle the situation of
th same-named field of the outer and intern classes. Path and name are separated by "|||".