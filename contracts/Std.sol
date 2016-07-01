contract Standard {

}
contract abstract{}

contract Owned is abstract {
  address owner;
  function owned() {
    owner = msg.sender;
  }
  function changeOwner(address newOwner) onlyOwner {
    owner = newOwner;
  }
  modifier onlyOwner() {
    if (tx.origin==owner) _
  }
  modifier msgOnlyOwner() {
    if (msg.sender==owner) _
  }

  function getOwner() constant returns (address){
    return owner;
  }
}

contract Mortal is Owned {
  function kill() {
    if (msg.sender == owner) suicide(owner);
  }
}

  contract NameRegistryDB is Owned, Mortal{

    mapping (string=>address) registry;

    function NameRegistryDB(){
       owner = msg.sender;
    }

      function addMapping(string _name,address _address) onlyOwner {
          registry[_name]=_address;
      }

      function getMapping(string _name) constant returns (address){
          return registry[_name];
      }
  }
contract StandardContract is Owned,Mortal{
    NameRegistryDB registry;

    function activateContract(NameRegistryDB _registry){
        registry = _registry;
    }


}
