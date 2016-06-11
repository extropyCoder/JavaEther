contract tokenRecipient {
  function receiveApproval(address _from, uint256 _value, address _token, bytes _extraData);
 }

contract SimpleCoin {
    /* Public variables of the token */
    string public name;
    string public symbol;
    string public version;
    uint8 public decimals;
    uint256 public totalSupply;

    mapping (address => uint256) public balanceOf;
    mapping (address => mapping (address => uint256)) public allowance;
    mapping (address => mapping (address => uint256)) public spentAllowance;

    event Transfer(address indexed from, address indexed to, uint256 value);


    function SimpleCoin() {
        balanceOf[msg.sender] = 1000000;
        totalSupply = 1000000;
        name = "Simple Coin";
        symbol = "SCN";
        decimals = 2;
        version = "1.0";
    }


    function transfer(address _to, uint256 _value) {
        if (balanceOf[msg.sender] < _value) throw;
        if (balanceOf[_to] + _value < balanceOf[_to]) throw;
        balanceOf[msg.sender] -= _value;
        balanceOf[_to] += _value;
        Transfer(msg.sender, _to, _value);
    }


    function approveAndCall(address _spender, uint256 _value, bytes _extraData) returns (bool success) {
        allowance[msg.sender][_spender] = _value;
        tokenRecipient spender = tokenRecipient(_spender);
        spender.receiveApproval(msg.sender, _value, this, _extraData);
        return true;
    }


    function transferFrom(address _from, address _to, uint256 _value) returns (bool success) {
        if (balanceOf[_from] < _value) throw;
        if (balanceOf[_to] + _value < balanceOf[_to]) throw;
        if (spentAllowance[_from][msg.sender] + _value > allowance[_from][msg.sender]) throw;
        balanceOf[_from] -= _value;
        balanceOf[_to] += _value;
        spentAllowance[_from][msg.sender] += _value;
        Transfer(_from, _to, _value);
        return true;
    }


    function () {
        throw;
    }
}
