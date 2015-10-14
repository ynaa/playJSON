define(['react', 'jquery', 'myInput', 'common'], function(React, Router, MyInput, Common){

  var ExpenseTypeHeader = React.createClass({
    render: function() {
      return (<header><hgroup><h2>Registrer ny utgiftstype</h2></hgroup></header>);
  }
  });

  var ExpenseType = React.createClass({
      onChange: function(cid){
        console.log("Changing " + this.props.id);

        var value = cid.target.value;

      	var expType = this.props.expType;
        expType[cid.target.name] = value;
        this.props.onTypeEdit(expType);
      },
      onDelete: function(){
          this.props.onTypeDelete(this.props.id);
      },
      render: function() {
          return (
              <div className="Row">
                  <div className="Cell">
                      <MyInput key={this.props.expType._id} name="typeName" value={this.props.expType.typeName} onBlur={this.onChange} />
                  </div>

                  <input type="hidden" name="id" value={this.props.expType._id} />

                  <div className="Cell">
                      <a href={"#Details/" + this.props.expType._id} title="">Vis</a>
                  </div>
                  <div className="Cell">
                      <button onClick={this.onDelete} >Slett</button>
                  </div>
              </div>
          );
      }
  });

  var ExpenseTypeForm = React.createClass({
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

  var ExpenseTypeList = React.createClass({
      render: function() {
          var onDelete = this.props.onTypeDelete;
          var onEdit = this.props.onTypeEdit;
          var size = this.props.data.length;
          var rows = this.props.data.map(function(expType, index) {
              return (
                  <ExpenseType onTypeDelete={onDelete} onTypeEdit={onEdit} expType={expType} key={index} />
              );
          });
          return (
              <div>
                  <h2>Liste med utgiftstyper, {size} type(r)</h2>
                  <div className="Table"></div>
                  <div className="Heading">
                      <div className="Cell"><p>Utgiftsnavn</p></div>
                      <div className="Cell"><p>Detaljer</p></div>
                      <div className="Cell"><p>Slett</p></div>
                  </div>
                  {rows}

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
                this.setState({data: data.expTypesList});
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
        handleTypeDelete: function(theId) {
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
        handleTypeEdit: function(editType){
          var types = this.state.data;
          var json = JSON.stringify(editType);
          this.setState({data: types}, function() {
              // `setState` accepts a callback. To avoid (improbable) race condition,
              // `we'll send the ajax request right after we optimistically set the new
              // `state.
              $.ajax({
                  url: "/expenseTypes/edit/" + editType._id,
                  contentType: "application/json; charset=utf-8",
                  type: 'POST',
                  data: json,
                  success: function(data) {
                      //this.loadExpenseTypesFromServer();
                  }.bind(this),
                  error: function(xhr, status, err) {
                      console.error(this.props.url, status, err.toString());
                  }.bind(this)
              });
          });
        },
        getInitialState: function() {
            return {data: []};
        },
        componentDidMount: function() {
            this.loadExpenseTypesFromServer();
        },
        render: function() {
            return (
                <div>
                    <ExpenseTypeHeader/>
                    <ExpenseTypeForm onTypeSubmit={this.handleNewTypeSubmit} />
                    <ExpenseTypeList onTypeDelete={this.handleTypeDelete} onTypeEdit={this.handleTypeEdit} data={this.state.data}/>
                </div>
            );
        }
    });

  return ExpenseTypeWrapper;
});
//
//React.render(
//    <div>
//        <ExpenseTypeWrapper url="/expenseTypes/list" />
//    </div>,
//    document.getElementById('content')
//);
