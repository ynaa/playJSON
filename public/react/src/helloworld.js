React.render(
  <h1>Hello, world 123!</h1>,
  document.getElementById('content')
);

// ExpenseType
var CommentList = React.createClass({
  render: function() {
    return (
      <div className="commentList">
        Hello, world! I am a CommentList.
      </div>
    );
  }
});