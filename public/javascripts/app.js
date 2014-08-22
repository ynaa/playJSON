var myEconomyApp = angular.module('myEconomyApp', [ 'ngRoute',
		'expTypeControllers', 'expDetControllers', 'purchasesControllers',
		'overviewController', "fileuploadController" ]);

myEconomyApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/list', {
		templateUrl : 'expenseTypes/listTypes.html',
		controller : 'ExpenseTypesController'
	}).when('/detlist', {
		templateUrl : 'expenseDetails/listDetails.html',
		controller : 'ExpenseDetailsController'
	}).when('/detlist/:expDetId', {
		templateUrl : 'expenseDetails/listDetails.html',
		controller : 'ExpenseDetailsController'
	}).when('/purchaselist', {
		templateUrl : 'purchases/purchaseList.html',
		controller : 'PurchaseController'
	}).when('/overview', {
		templateUrl : 'overview/overview.html',
		controller : 'OverviewController'
	}).when('/fileupload', {
		templateUrl : 'fileupload/fileupload.html',
		controller : 'FileuploadController'
	}).otherwise({
		redirectTo : '/list'
	});
} ]);

myEconomyApp.directive('datepicker', function() {
	return {
		restrict : 'A',
		require : 'ngModel',
		link : function(scope, element, attrs, ngModelCtrl) {
			$(function() {
				element.datepicker({
					dateFormat : 'dd.mm.yy',
					onSelect : function(date) {
						ngModelCtrl.$setViewValue(date);
						scope.$apply();
					}
				});
			});
		}
	}
});

myEconomyApp.filter('orderObjectBy', function() {
	return function(items, reverse) {
		var filtered = [];
		angular.forEach(items, function(item) {
			filtered.push(item);
		});
		var reversed = reverse ? 1 : -1;
		filtered.sort(function(a, b) {
			if(isNaN(a) && !isNaN(b) ) {
				if(a.startsWith(b)){
					return reversed;
				}
				else {
					return a > b ? 1 : -1;
				}
			}
			else if(!isNaN(a) && isNaN(b) ) {
				if(b.startsWith(a)){
					return reversed;
				}
				else {
					return a > b ? 1 : -1;
				}
			}
			else{	
				return (a > b ? 1 : -1);
			}
		});
		if (reverse)
			filtered.reverse();
		return filtered;
	};
});
