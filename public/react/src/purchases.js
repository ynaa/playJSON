define(['react', 'jquery', 'myInput', 'common', 'datepicker', 'moment'], function(React, Router, MyInput, Common, DatePicker, Moment){

	var PurchasesWrapper = React.createClass({
		loadPurchasesFromServer: function(params) {
			var oldData = this.state.data;
			$.ajax({
				url: '/expenseTypes/list',
				dataType: 'json',
				success: function(data) {
					this.setState({expTypesList: data.expTypesList});
				}.bind(this),
				error: function(xhr, status, err) {
					console.error(this.props.url, status, err.toString());
				}.bind(this)
			});
			$.ajax({
				url: this.props.url,
				dataType: 'json',
				success: function(data) {
					this.setState({purchasesList: data.purchasesList, expDetList: data.expDetList});
				}.bind(this),
				error: function(xhr, status, err) {
					console.error(this.props.url, status, err.toString());
				}.bind(this)
			});
	  },
	  handlePurchaseEdit: function(purchase){
	    if(purchase.textcode == null){
	      purchase.textcode = '';
	      purchase.archiveref = '';
	      purchase.account = '';
	    }
	    var json = JSON.stringify(purchase);
	    this.setState({data: this.state.data}, function() {
	        $.ajax({
	            url: "/purchases/edit/" + purchase._id,
	            contentType: "application/json; charset=utf-8",
	            type: 'POST',
	            data: json,
	            success: function(data) {
	            }.bind(this),
	            error: function(xhr, status, err) {
	                console.error(this.props.url, status, err.toString());
	            }.bind(this)
	        });
	    });
	  },
		filterPurchases: function(expType, expDet, start, end){

			var url = "/purchases/list?";
			if(expType) {
				url += "&expType=" + expType;
			}
			if(expDet) {
				url += "&expDet=" + expDet;
			}
			if(start) {
				url += "&start=" + Common.createSearchDate(start.toDate());
			}
			if(end) {
				url += "&stop=" + Common.createSearchDate(end.toDate());
			}
			$.ajax({
	      url: url,
	      dataType: 'json',
	      success: function(data) {
					this.setState({purchasesList: data.purchasesList, expDetList: data.expDetList});
	      }.bind(this),
	      error: function(xhr, status, err) {
          console.error(this.props.url, status, err.toString());
	      }.bind(this)
	    });
		},
	  handlePurchaseDelete: function(purhcase_id){
	    console.log("Sletter");
	    var purchasesList = this.state.data.purchasesList;
	    var expDetList = this.state.data.expDetList;
	    var expTypesList = this.state.data.expTypesList;
	    var allData = purchasesList.items;
	    var purhcase = allData.find(function(id, value){return id=purhcase_id});
	    if(confirm("Er du sikker på du vil slette  " + purhcase.description + "?")){
	        $.ajax({
	            url: "/purchases/delete/" + purhcase_id,
	            type: 'DELETE',
	            data: {},
	            success: function(data) {
	                allData.splice(purhcase, 1);
	                purchasesList.items = allData;
	                var newData = {expDetList : expDetList, purchasesList : purchasesList, expTypesList: expTypesList}
	                this.setState({data: newData});
	            }.bind(this),
	            error: function(xhr, status, err) {
	                console.error(this.props.url, status, err.toString());
	            }.bind(this)
	        });
	    }
	  },
	  getInitialState: function() {
      return {
        'purchasesList' : {'items': []},
        'expDetList' : [],
				'expTypesList': []
      };
	  },
	  componentDidMount: function() {
	      this.loadPurchasesFromServer();
	  },
	  render: function() {
      return (
        <div>
          <PurchasesFilterForm expTypes={this.state.expTypesList} expDetails={this.state.expDetList} filterPurchases={this.filterPurchases}/>
          <PurchaseTotal sum={Common.calculateSum(this.state.purchasesList.items) } />
          <PurchasesList onEdit={this.handlePurchaseEdit} onDelete={this.handlePurchaseDelete} purchases={this.state.purchasesList} expDets={this.state.expDetList}/>
        </div>
      );
	  }
});

var PurchaseTotal = React.createClass({
  render: function(){
    return <h2>Totalsum for dette søket er {this.props.sum}</h2>
  }
});


var Purchase = React.createClass({
    onBlur: function(cid){
      var purchase = this.props.purchase;
      var value = cid.target.value;
      if(cid.target.name == "expenseDetail"){
    		value = findById(value, this.props.expenseDetails);
      }

      if(cid.target.name == "amount"){
        value = parseFloat(value);
      }
      purchase[cid.target.name] = value;
      this.props.onEdit(purchase);
    },
    onDelete: function(){
      this.props.onDelete(this.props.purchase._id);
    },
    render: function() {
      return (
        		<div className="Row">
                <div className="Cell">
                    <input readOnly key={this.props.purchase._id} name="description" value={this.props.purchase.description} />
                </div>
                <div className="Cell">
                	<input readOnly key={this.props.purchase._id} name="Date" value={Common.createDate(this.props.purchase.bookedDate)} />
                </div>
                <div className="Cell">
                	<input readOnly key={this.props.purchase._id} name="amount" value={this.props.purchase.amount}/>
                </div>
                <div className="Cell">
                	{Common.createDetailSelect("expenseDetail", this.props.expenseDetails,
                			this.props.purchase.expenseDetail._id,
                			this.onBlur, false)}
                </div>
                <div className="Cell">
                    <button onClick={this.onDelete} >Slett</button>
                </div>
            </div>
        );
    }
});

var PurchasesFilterForm = React.createClass({
	onBlur: function(expType, expDet, start, end){
		this.props.filterPurchases(expType, expDet, start, end);
	},
	getInitialState: function() {
    return {
      startdate: null,
      enddate: null,
      expType: null,
      expDet: null
    };
  },
  handleExpTypeChange: function(expType) {
    this.setState({ expType: expType.target.value });
		this.onBlur(expType.target.value, this.state.expDet, this.state.startdate, this.state.enddate);
  },
  handleExpDetChange: function(expDet) {
    this.setState({ expDet: expDet.target.value });
		this.onBlur(this.state.expType, expDet.target.value, this.state.startdate, this.state.enddate);
  },
  handleStartDateChange: function(date) {
    this.setState({ startdate: date });
		this.onBlur(this.state.expType, this.state.expDet, date, this.state.enddate);
  },
  handleEndDateChange: function(date) {
    this.setState({ enddate: date });
		this.onBlur(this.state.expType, this.state.expDet, this.state.startdate, date);
  },
  render: function() {
        return (
          <div>
            <h2>Filter</h2>
            <form name="regform">
              <div className="Table">
                <div className="Row">
                  <div className="FilterCell">
				              <dl>
                        <dd>Utgiftsttype</dd>
                        <dt>
													{Common.createSelect("expType", this.props.expTypes,
				                			this.state.expType,
				                			this.handleExpTypeChange, false, [{ id: '', name: 'Velg utgiftsttype' }])}
                        </dt>
                      </dl>
                    </div>
                    <div className="FilterCell">
                    <dl>
                      <dd>Utgiftsdetalj</dd>
                      <dt>
			                	{Common.createDetailSelect("expenseDetail", this.props.expDetails,
														this.state.expDet,
			                			this.handleExpDetChange, false, [{ id: '-2', name: 'Ingen' }, { id: '', name: 'Velg utgiftstdetalj' }])}
                    </dt>
                  </dl>
                </div>
                <div className="FilterCell">
                  <dl>
                    <dd>Startdato</dd>
                    <dt>
										<DatePicker id="startdate" name="start" key="startdate" selected={this.state.startdate} onChange={this.handleStartDateChange} dateFormat="DD.MM.YYYY"/>
                    </dt>
                  </dl>
                </div>
                <div className="FilterCell">
                  <dl>
                    <dd>Sluttdato</dd>
                    <dt>
											<DatePicker id="enddate" name="end" key="enddate" selected={this.state.enddate} onChange={this.handleEndDateChange} dateFormat="DD.MM.YYYY"/>
                    </dt>
                  </dl>
                </div>
              </div>
            </div>
          </form>
        </div>
        );
    }
});


var PurchasesList = React.createClass({
  render: function() {
    var rows = {};
    var size = 0;
    if(this.props.purchases.items){
      var purchases = this.props.purchases.items;
      var expenseDetails = this.props.expDets;
      var onDelete = this.props.onDelete;
      var onEdit = this.props.onEdit;
      size = purchases.length;
      rows =  this.props.purchases.items.map(function(purchase) {
        return (
          <Purchase onDelete={onDelete} onEdit={onEdit} purchase={purchase} key={purchase._id} expenseDetails={expenseDetails} />
        );
      });
    }
    return (
      <div>
        <h2>Liste med utgiftstyper, {size} type(r)</h2>
        <div className="Table">
          <div className="Heading">
            <div className="Cell"><p>Beskrivelse</p></div>
            <div className="Cell"><p>Dato</p></div>
            <div className="Cell"><p>Beløp</p></div>
            <div className="Cell"><p>Detalj</p></div>
            <div className="Cell"><p>Slett</p></div>
          </div>
					{rows}
        </div>
      </div>
    );
  }
});


return PurchasesWrapper;
});
