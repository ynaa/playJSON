require.config({
	deps: ["main"],
  paths: {
    "react": "../bower_components/react/react-with-addons",
    "JSXTransformer": "../bower_components/react/JSXTransformer",
    "jsx": "../bower_components/requirejs-react-jsx/jsx",
    "text": "../bower_components/requirejs-text/text",
    "jquery": "//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min",
		"datepicker": "dist/react-datepicker",
		"moment": "node_modules/moment/moment",
		"react-onclickoutside": "node_modules/react-onclickoutside/index",
		"reactRouter": "umd/ReactRouter"
  },

  shim : {
		JSXTransformer: {
			exports: "JSXTransformer"
		}
  },
	jsx: {
		fileExtension: '.js'
	}
});


require(['common'], function(){
	require(['react', 'jsx!app', 'jsx!menu', 'reactRouter'], function(React, App, Menu, ReactRouter){


		var Router = ReactRouter.Router;
		var Route = ReactRouter.Route;

		React.render((
			<Router >
				<Route path="/" component={Menu}>

				</Route>
			</Router>
		), document.body);


	});

//React.render(<App/>, document.body);


});

define(['jquery'], function($) {
    //the jquery.alpha.js and jquery.beta.js plugins have been loaded.
    $(function() {
        $('body');
    });

});
