
var MyInput = React.createClass({
	getInitialState: function() {
	    return {
	    	field: '',
	    	name: ''
		};
	},
	componentWillMount: function() {
	    this.setState({
	    	field: this.props.value,
	    	name: this.props.name
	    });
	  },
	onChange: function(event){
		this.setState({field: event.target.value});
	},
	render: function() {
    	var field = this.state.field;
    	var name = this.state.name;
        return (
            <input name={name} value={field} onBlur={this.props.onBlur} onChange={this.onChange}/>
        );
    }
});


function debug(object){
	var output = '';
	for (var property in object) {
	  output += property + ': ' + object[property]+'; ';
	}
	console.log(output);
}

function findExpType(value, expTypeList){
	for (i = 0; i < expTypeList.length; i++) {
	    if(expTypeList[i]._id == value){
	    	return expTypeList[i];
	    }
	}
	return null;
}
