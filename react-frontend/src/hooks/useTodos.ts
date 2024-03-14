import { TodoQuery } from "../App";
import useData from "./useData";

export interface Platform {
    id: number
    name: string
    slug: string
}

export interface Todo {
    id: number
    userId: number
    name: string
    priority: string
    status: string
    untilDate: Date
  }


  const useGames = (todoQuery: TodoQuery) => {
    const endpoint = `/todos/${todoQuery.todoId}/user/${todoQuery.userId}`;
    return useData<Todo[]>(endpoint, {}, [todoQuery]);
  };

export default useGames;