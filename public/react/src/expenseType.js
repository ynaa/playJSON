var ExpenseTypeHeader = React.createClass({
  render: function() {
    return (
		<header>
			<hgroup>
				<h2>Registrer ny utgiftstype 12</h2>
			</hgroup>
		</header>
    );
  }
});

var ExpenseType = React.createClass({
	onChange: function(){
		
	},
  render: function() {
    return (
		<div className="Row">
	      <div className="Cell">
	        <input name="typeName1" value={this.props.typeName} onChange={this.onChange} />
	      </div>
	      <div className="Cell">
	        <a href="#/detlist/}}" title="">Vis</a>
	      </div>
	      <div className="Cell">
	        <button>Slett</button>
	      </div>
      </div>
    );
  }
});

var ExpenseTypeWrapper = React.createClass({
	loadExpenseTypesFromServer: function() {
	    $.ajax({
	      url: this.props.url,
	      dataType: 'json',
	      success: function(data) {
    	    console.log("Heisann: ");
	        this.setState({data: data.expTypesList});
	        console.log("HVa er vedien her: " + this.state.data);
	    }.bind(this),
	    error: function(xhr, status, err) {
	      console.log("Er vi her?");
	      console.error(this.props.url, status, err.toString());
	    }.bind(this)
	  });
	},
	getInitialState: function() {
	  this.setState({data: []});
	  return {data: []};
	},
	componentDidMount: function() {
	  this.loadExpenseTypesFromServer();
	},
	render: function() {
	  return (
        <div>
			<ExpenseTypeHeader/>
			<ExpenseTypeForm/>
			<ExpenseTypeList data={this.state.data}/>
		</div>
	    );
	  }
	});


var ExpenseTypeForm = React.createClass({
  handleSubmit: function(e) {
    e.preventDefault();
    var typeName = this.refs.typeName.getDOMNode().value.trim();
    console.log("Typename = " + typeName);

    this.refs.typeName.getDOMNode().value = '';
    return;
  },
  render:
    function() {
        return (
    		<form name='regform' onSubmit={this.handleSubmit}>
        		<input name='typeName' placeholder='Navn på utgiftstype' ref='typeName'/>
        		<button type='submit' className='btn btn-primary' value="Post">Legg til</button>
    		</form>
        );
  }
});

var ExpenseTypeList = React.createClass({
  render: function() {
	  console.log("Creating expenseTypeList" + this.props.data);
    var rows = this.props.data.map(function(expType, index) {
      return (
        <ExpenseType typeName={expType.typeName} key={index} />
      );
    });
    return (
		<div>
			<h2>Liste med utgiftstyper, 1 type(r)</h2>
			<div className="Table"></div>
			<div className="Heading">
				<div className="Cell"><p>Utgiftsnavn</p></div>
				<div className="Cell"><p>Detaljer</p></div>
				<div className="Cell"><p>Slett</p></div>
			</div>
			 <div className="commentList">
		        {rows}
		      </div>
		</div>
	    );
	  }
});

React.render(
	<div>
		<ExpenseTypeWrapper url="expType.json" />
	</div>,
  document.getElementById('content')
);