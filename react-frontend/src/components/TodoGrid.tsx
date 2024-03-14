import { SimpleGrid, Text } from "@chakra-ui/react";
import useTodos from "../hooks/useTodos";
import { TodoQuery } from "../App";
import TodoContainer from "./TodoContainer";
import TodoSkeleton from "./TodoSkeleton";
import TodoCard from "./TodoCard";

interface Props {
  todoQuery: TodoQuery;
}

const TodoGrid = ({ todoQuery }: Props) => {
  const { data, error, isLoading } = useTodos(todoQuery);

  const skeletons = [1, 2, 3];

  if (error) return <Text>{error}</Text>;

  return (
    <SimpleGrid columns={1} padding="10px" spacing={5}>
      {isLoading &&
        skeletons.map((skeleton) => (
          <TodoContainer key={skeleton}>
            {" "}
            <TodoSkeleton />{" "}
          </TodoContainer>
        ))}
      {data.map((todo) => (
        <TodoContainer key={todo.id}>
          <TodoCard todo={todo} />
        </TodoContainer>
      ))}
    </SimpleGrid>
  );
};

export default TodoGrid;
