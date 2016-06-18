contract abstract{}

contract Owned is abstract {
  address owner;
  function owned() {
    owner = msg.sender;
  }
  function changeOwner(address newOwner) onlyowner {
    owner = newOwner;
  }
  modifier onlyowner() {
    if (msg.sender==owner) _
  }
}

contract Mortal is Owned {
  function kill() {
    if (msg.sender == owner) suicide(owner);
  }
}
