var ExpenseTypeHeader = React.createClass({
  render: function() {
    return (<header><hgroup><h2>Registrer ny utgiftstype</h2></hgroup></header>);
}
});

var ExpenseDetail = React.createClass({
    onChange: function(){
        console.log("Changing");
    },
    onDelete: function(){
        this.props.onTypeDelete(this.props.id);
    },
    render: function() {
        return (
            <div className="Row">
                <div className="Cell">
                    <input name="detName1" value={this.props.expDet.description} />
                </div>
                <div className="Cell">
                    <input name="detName1" value={this.props.expDet.searchTags} />
                </div>
                <div className="Cell">
                    <input name="expType1" value={this.props.expDet.expenseType.typeName} />
                </div>
                <div className="Cell">
                    <a href={"#/purchaselist/" + this.props.id} title="">Vis</a>
                </div>
                <div className="Cell">
                    <button onClick={this.onDelete} >Slett</button>
                </div>
            </div>
        );
    }
});

var ExpenseDetailWrapper = React.createClass({
    loadExpenseTypesFromServer: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            success: function(data) {
                this.setState({data: data.result.expDetList});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleNewTypeSubmit: function(newType) {
        var types = this.state.data;
        types.push(newType);
        var json = JSON.stringify(newType);
        this.setState({data: types}, function() {
            // `setState` accepts a callback. To avoid (improbable) race condition,
            // `we'll send the ajax request right after we optimistically set the new
            // `state.
            $.ajax({
                url: "/expenseTypes/add",
                contentType: "application/json; charset=utf-8",
                type: 'POST',
                data: json,
                success: function(data) {
                    //this.setState({data: data});
                    this.loadExpenseTypesFromServer();
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
        });
    },
    handleNewTypeDelete: function(theId) {
        var allData = this.state.data;
        var expType = allData.find(function(id, value){return id=theId});
        if(confirm("Er du sikker på du vil slette  " + expType.typeName + "?")){
            $.ajax({
                url: "/expenseTypes/delete/" + theId,
                type: 'DELETE',
                data: {},
                success: function(data) {
                    allData.splice(expType, 1);
                    this.setState({data: allData});

                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
        }
    },
    getInitialState: function() {
        this.setState({data: []});
        return {data: []};
    },
    componentDidMount: function() {
        this.loadExpenseTypesFromServer();
    },
    render: function() {
        /*
                <ExpenseTypeHeader/>
                <ExpenseTypeForm onTypeSubmit={this.handleNewTypeSubmit} />
        */
        return (
            <div>
                <ExpenseDetailFilter />
                <ExpenseDetailList onTypeDelete={this.handleNewTypeDelete} data={this.state.data}/>
            </div>
        );
    }
});


var ExpenseDetailForm = React.createClass({
    handleSubmit: function(e) {
        e.preventDefault();
        var typeName = this.refs.typeName.getDOMNode().value.trim();
        this.props.onTypeSubmit({typeName: typeName});
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


var ExpenseDetailFilter = React.createClass({
    filter: function() {
        console.log("Filter");
    },
    render: function() {
        return (
            <div>
                <h4>Filtrer på type</h4>
                <div>
                    <select name="expType" ng-model="filterExpType"
                    ng-options="expType._id as expType.typeName for expType in expenseTypes" ng-change={this.filter}>
                        <option value="">-- Filtrer utgiftsttype --</option>
                    </select>

                </div>
            </div>
        );
    }
});

var ExpenseDetailList = React.createClass({
    render: function() {
        var onDelete = this.props.onTypeDelete;
        var size = this.props.data.length;
        var rows = this.props.data.map(function(expDet, index) {
            return (
                <ExpenseDetail expDet={expDet} key={index} />
            );
        });
        return (
            <div>
                <h2>Liste med utgiftstyper, {size} type(r)</h2>
                <div className="Table">
                    <div className="Heading">
                        <div className="Cell"><p>Beskrivelse</p></div>
                        <div className="Cell"><p>Søkeord</p></div>
                        <div className="Cell"><p>Type</p></div>
                        <div className="Cell"><p>Vis utgifter</p></div>
                        <div className="Cell"><p>Slett</p></div>
                    </div>
                    <div className="commentList">
                        {rows}
                    </div>
                </div>
            </div>
        );
    }
});

React.render(
    <div>
        <ExpenseDetailWrapper url="/expenseDetails/list" />
    </div>,
    document.getElementById('content')
);