define(['react', 'menu', 'content'], function(React, Menu, Content){
	var App = React.createClass({
			render: function() {
					return <div><Menu/><div id='content'>
					
					</div></div>;
			}
	});

  return App;
});
