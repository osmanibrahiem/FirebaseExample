# FirebaseExample
This is an example for android application connected with firebase database

### • Step 1 : Get a DatabaseReference
To read or write data from the database, you need an instance of DatabaseReference:
```ruby
private DatabaseReference mReference;
// ...
mReference = FirebaseDatabase.getInstance().getReference();
```

### • Step 2 : Write and read data
Assuming you have a model class `Data.class` with attributes `String id, title, desc;`
```ruby
// Data.class
public class Data {

    private String id, title, desc;

    public Data() {
        // Default constructor required for calls to DataSnapshot.getValue(Data.class)
    }
    
    ...
    
}
```

#### • Step 2.1 : Write data
You can add an item with `setValue()` as follows:
```ruby
private void writeNewItem(String title, String desc) {
    Data item = new Data();
    item.setTitle(title);
    item.setDesc(desc);

    mReference.child("Data").push().setValue(item);
}
```

### • Step 3 : Listen for value events
To read data at a path and listen for changes, use the `addValueEventListener()` or `addListenerForSingleValueEvent()` method to add a `ValueEventListener` to a `DatabaseReference`.

| Listener | Event callback | Typical usage | 
| ------ | ------ | ------ |
| `ValueEventListener` | `onDataChange()` | Read and listen for changes to the entire contents of a path (insert, update, delete or move). |
| `ChildEventListener` | `onChildAdded()` | Read and listen for inserting to the entire contents of a path (insert only). |
| `ChildEventListener` | `onChildChanged()` | Listen for updating the entire contents of a path (update only). |
| `ChildEventListener` | `onChildRemoved()` | Listen for removing the entire contents of a path (remove only). |
| `ChildEventListener` | `onChildMoved()` | Listen for moving the entire contents of a path to another path (move only). |

The following example demonstrates a social blogging application retrieving the details of a post from the database:
```ruby
ValueEventListener postListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get Post object and use the values to update the UI
        Post post = dataSnapshot.getValue(Post.class);
        // ...
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        // ...
    }
};
mPostReference.addValueEventListener(postListener);
```

other example to read list item by item and listen to changes on the path:
```ruby
ChildEventListener dataListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.addData(item);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.updateData(item);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.removeData(item);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    
mReference.addChildEventListener(dataListener);
```

And this is our Database
![Database](https://github.com/osmanibrahiem/FirebaseExample/blob/master/database.PNG)

