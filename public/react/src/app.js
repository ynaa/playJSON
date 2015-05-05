var Menu = React.createClass({
	render: function() {
		return (
			<nav id='web-nav'> 
				<ul id='menu'>
					<li className='active'><a href='#Types'><span>Typer</span></a></li>
					<li><a href='#Details'><span>Detaljer</span></a></li>
					<li><a href='#Purchases'><span>Utgifter</span></a></li>
					<li><a href='#Overview'><span>Oversikt</span></a></li>
					<li className='last'><a href='#Upload'><span>Last opp</span></a></li>
				</ul>
			</nav>
		);
	}
});

var Content = React.createClass({
	getInitialState: function() {
        return {
            page: null
        }
    },
    componentDidMount: function() {
        router.addRoute('', function() {
            this.setState({page: <ExpenseTypeWrapper url='/expenseTypes/list' />});
        }.bind(this));
        router.addRoute('Types', function(id) {
            this.setState({page: <ExpenseTypeWrapper url='/expenseTypes/list' />});
        }.bind(this));
        router.addRoute('Details', function(id) {
            this.setState({page: <ExpenseDetailWrapper url="/expenseDetails/list" />});
        }.bind(this));
        router.addRoute('Details/:id', function(id) {
        	var url='/expenseDetails/list/' + id; 
            this.setState({page: <ExpenseDetailWrapper selectedDetail={id} url={url} />});
        }.bind(this));
        router.addRoute('Purchases', function(id) {
            this.setState({page: <ExpenseTypeWrapper url='/expenseTypes/list' />});
        }.bind(this));
        router.addRoute('Overview', function(id) {
            this.setState({page: <ExpenseTypeWrapper url='/expenseTypes/list' />});
        }.bind(this));
        router.addRoute('Upload', function(id) {
            this.setState({page: <ExpenseTypeWrapper url='/expenseTypes/list' />});
        }.bind(this));
        router.start();
    },
    render: function() {
        return this.state.page;
    }
});

var App = React.createClass({
    render: function() {
        return <div><Menu/><div id='content'><Content/></div></div>;
    }
});
React.render(<App/>, document.body);