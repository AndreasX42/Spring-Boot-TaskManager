import React, { useState, useEffect } from 'react';
import './App.css';
import User from './User';
import Todo from './Todo';

function App() {
  // Example user and todo data (replace with your actual data)
  const initialUsers = [
    { id: 1, username: 'user1' },
    { id: 2, username: 'user2' },
    { id: 3, username: 'user3' },
  ];

  const initialTodos = [
    { id: 1, title: 'Task 1' },
    { id: 2, title: 'Task 2' },
    { id: 3, title: 'Task 3' },
  ];

  const [users, setUsers] = useState(initialUsers);
  const [todos, setTodos] = useState(initialTodos);

  return (
    <div className="App">
      <User users={users} />
      <Todo todos={todos} />
    </div>
  );
}

export default App;
