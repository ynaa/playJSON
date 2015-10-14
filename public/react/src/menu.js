define(['react'], function(React){

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

	return Menu;
});
