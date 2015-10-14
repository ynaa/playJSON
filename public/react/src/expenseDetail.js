define(['react', 'jquery', 'myInput', 'common'], function(React, Router, MyInput, Common){

  var ExpenseTypeHeader = React.createClass({
    render: function() {
      return (<header><hgroup><h2>Registrer ny utgiftstype</h2></hgroup></header>);
  }
  });

  var ExpenseDetail = React.createClass({
      onBlur: function(cid){
      	var expDet = this.props.expDet;
      	var value = cid.target.value;
      	if(cid.target.name == "expenseType"){
      		value = findById(value, this.props.expTypeList);
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
          this.props.onDetailDelete(this.props.expDet._id);
      },
      render: function() {
        return (
          		<div className="Row">
                  <div className="Cell">
                      <MyInput key={this.props.expDet._id} name="description" value={this.props.expDet.description} onBlur={this.onBlur}/>
                  </div>
                  <div className="Cell">
                  	<MyInput key={this.props.expDet._id} name="searchTags" value={this.props.expDet.searchTags} onBlur={this.onBlur}/>
                  </div>
                  <div className="Cell">

                  	{Common.createSelect("expenseType", this.props.expTypeList,
                  			this.props.expDet.expenseType._id,
                  			this.onBlur, false)}
                  </div>
                  <div className="Cell">
                      <a href={"#Purchases?expDet=" + this.props.expDet._id} title="">Vis</a>
                  </div>
                  <div className="Cell">
                      <button onClick={this.onDelete} >Slett</button>
                  </div>
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
      	var select = Common.createSelect("", this.props.data.expTypesList,
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
      var rows = {};
      var size = 0;
      if(this.props.data.expDetList){
      	var expTypeList = this.props.data.expTypesList;
      	var edit = this.props.onDetailEdit;
        var onDelete = this.props.onDetailDelete;
        size = this.props.data.expDetList.length;
        rows = this.props.data.expDetList.map(function(expDet) {
          return (
            <ExpenseDetail onDetailEdit={edit} onDetailDelete={onDelete} expDet={expDet} key={expDet._id} expTypeList={expTypeList} />
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
          {rows}
        </div>
      </div>
      );
    }
  });

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


    var ExpenseDetailWrapper = React.createClass({
        loadDataFromServer: function(expTypeId) {
        	var url = this.props.url;
        	if(expTypeId != undefined && expTypeId != ''){
        		url = '/expenseDetails/list' + '/' + expTypeId
        	}
          else{
            url = '/expenseDetails/list'
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
                $.ajax({
                    url: "/expenseDetails/edit/" + newType._id,
                    contentType: "application/json; charset=utf-8",
                    type: 'POST',
                    data: json,
                    success: function(data) {
                        this.loadDataFromServer(this.props.selectedDetail);
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
                        this.loadDataFromServer(this.props.selectedDetail);
                    }.bind(this),
                    error: function(xhr, status, err) {
                        console.error(this.props.url, status, err.toString());
                    }.bind(this)
                });
            });
        },
        handleDetailDelete: function(theId) {
            var allData = this.state.data.expDetList;
            var expDet = allData.find(function(id, value){return id=theId});
            if(confirm("Er du sikker på du vil slette  " + expDet.description + "?")){
                $.ajax({
                    url: "/expenseDetails/delete/" + theId,
                    type: 'DELETE',
                    data: {},
                    success: function(data) {
                        allData.splice(expDet, 1);
                        this.state.data.expDetList = allData;
                        var newData = {expTypesList : this.state.data.expTypesList, expDetList : allData}
                        this.setState({data: newData});
                    }.bind(this),
                    error: function(xhr, status, err) {
                        console.error(this.props.url, status, err.toString());
                    }.bind(this)
                });
            }
        },
        getInitialState: function() {
            return {data: {
                        'expDetList' : [],
                        'expTypesList' : []
                }};
        },
        componentDidMount: function() {
            this.loadDataFromServer(this.props.selectedDetail);
        },
        render: function() {
            return (
                <div>
                    <ExpenseDetailFilter selectedDetail={this.props.selectedDetail} data={this.state.data} filterFunc={this.loadDataFromServer}/>
                    <ExpenseDetailList onDetailEdit={this.handleEditTypeSubmit} onDetailDelete={this.handleDetailDelete} data={this.state.data}/>
                </div>
            );
        }
    });

    return ExpenseDetailWrapper;

});
