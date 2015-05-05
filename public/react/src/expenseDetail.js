var ExpenseTypeHeader = React.createClass({
  render: function() {
    return (<header><hgroup><h2>Registrer ny utgiftstype</h2></hgroup></header>);
}
});

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

var ExpenseDetail = React.createClass({
    onBlur: function(cid){
    	var expDet = this.props.expDet;
    	var value = cid.target.value;
    	if(cid.target.name == "expenseType"){
    		value = findExpType(value, this.props.expTypeList);
    	}
    	if(expDet[cid.target.name] == value){
    		return;
    	}
    	if(cid.target.name == "searchTags"){
    		value = createJSONList(value);
    	}
    	expDet[cid.target.name] = value;
        this.props.onDetailEdit(expDet);
    },
    onDelete: function(){
        this.props.onTypeDelete(this.props.expDet._id);
    },
    render: function() {
        return (
        		<div className="Row">
                <div className="Cell">
                    <MyInput name="description" value={this.props.expDet.description} onBlur={this.onBlur}/>
                </div>
                <div className="Cell">
                	<MyInput name="searchTags" value={this.props.expDet.searchTags} onBlur={this.onBlur}/>
                </div>
                <div className="Cell">
                	{createSelect("expenseType", this.props.expTypeList, 
                			this.props.expDet.expenseType._id, 
                			this.onBlur, false)}
                </div>
                <div className="Cell">
                    <a href={"#/purchaselist/" + this.props.id} title="">Vis</a>
                </div>
                <div className="Cell">
                    <button onClick={this.onDelete} >Slett</button>
                </div>
            </div>
//    		<div className="Row">
//                <div className="Cell">
//                    <input name="description" value={description} onBlur={this.onBlur} onChange={this.onChange}/>
//                </div>
//                <div className="Cell">
//                    <input name="searchTags" value={searchTags}  onBlur={this.onBlur} onChange={this.onChange} />
//                </div>
//                <div className="Cell">
//                	{createSelect("expenseType", this.props.expTypeList, 
//                			expenseType._id, 
//                			this.onBlur, false)}
//                </div>
//                <div className="Cell">
//                    <a href={"#/purchaselist/" + this.props.id} title="">Vis</a>
//                </div>
//                <div className="Cell">
//                    <button onClick={this.onDelete} >Slett</button>
//                </div>
//            </div>
        );
    }
});

var ExpenseDetailWrapper = React.createClass({
    loadDataFromServer: function(expTypeId) {
    	var url = this.props.url;
    	if(expTypeId != undefined && expTypeId != ''){
    		url = url + '/' + expTypeId
    	}
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data) {
                this.setState({data: data.result});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleEditTypeSubmit: function(newType) {
    	var types = this.state.data.expDetList;
        types.push(newType);
        var json = JSON.stringify(newType);
        this.setState({data: types}, function() {
            // `setState` accepts a callback. To avoid (improbable) race condition,
            // `we'll send the ajax request right after we optimistically set the new
            // `state.
            $.ajax({
                url: "/expenseDetails/edit/" + newType._id,
                contentType: "application/json; charset=utf-8",
                type: 'POST',
                data: json,
                success: function(data) {
                    this.loadDataFromServer();
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
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
                    this.loadDataFromServer();
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
        return {data: {
                    'expDetList' : [],
                    'expTypesList' : []
            }};
    },
    componentDidMount: function() {
        this.loadDataFromServer();
    },
    render: function() {
        return (
            <div>
                <ExpenseDetailFilter selectedDetail={this.props.selectedDetail} data={this.state.data} filterFunc={this.loadDataFromServer}/>
                <ExpenseDetailList onDetailEdit={this.handleEditTypeSubmit} onTypeDelete={this.handleNewTypeDelete} data={this.state.data}/>
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
    filter: function(cid) {
    	var filterValue = cid.target.value;
        this.props.filterFunc(filterValue);
    },
    render: function() {
    	var select = createSelect("", this.props.data.expTypesList, 
    			this.props.selectedDetail, 
    			this.filter, true);
        return (
            <div>
                <h4>Filtrer på type</h4>
                <div>
                    {select}
                </div>
            </div>
        );
    }
});

var ExpenseDetailList = React.createClass({
    render: function() {
        var onDelete = this.props.onTypeDelete;
        var rows = {};
        var size = 0;
        if(this.props.data.expDetList){
        	var expTypeList = this.props.data.expTypesList;
        	var edit = this.props.onDetailEdit;
            size = this.props.data.expDetList.length;
            rows = this.props.data.expDetList.map(function(expDet, index) {
            return (
                <ExpenseDetail onDetailEdit={edit} expDet={expDet} key={index} expTypeList={expTypeList} />
            );
        });
        }

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

function createSelect(name, list, selectedDetail, filter, includeNone){
    var options = [];
    if(list == undefined){
    	return;
    }
    for (var i = 0; i < list.length; i++) {
        var option = list[i];
        options.push(
            <option key={i} value={option._id}>{option.typeName}</option>
        );
    }
    if(includeNone){
    	 options.unshift(
    	            <option key='' value=''>Ingen</option>
    	        );
    }
    return (
    		<select name={name} value={selectedDetail} 
    			onChange={filter}>{options}</select>);
}
function findExpType(value, expTypeList){
	for (i = 0; i < expTypeList.length; i++) {
	    if(expTypeList[i]._id == value){
	    	return expTypeList[i];
	    }
	}
	return null;
}

function createJSONList(temp){
	try {
		var tags = temp.split(",");
		var json = [];
		for (var i = 0; i < tags.length; i++) {
			json.push(tags[i]);
		}
		return json;
	} catch (e) {
		console.log(e);
		return temp;
	}
}
function debug(object){
	var output = '';
	for (var property in object) {
	  output += property + ': ' + object[property]+'; ';
	}
	console.log(output);
}
