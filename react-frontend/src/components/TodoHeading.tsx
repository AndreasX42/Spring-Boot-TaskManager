import { Heading } from "@chakra-ui/react";
import { GameQuery, TodoQuery } from "../App";

interface Props {
  todoQuery: TodoQuery;
}

const TodosHeading = ({ todoQuery }: Props) => {
  const heading = `${todoQuery.platform?.name || ""} ${
    gameQuery.genre?.name || ""
  } Todos`;

  return (
    <Heading as="h1" marginY={5} fontSize="5xl">
      {heading}
    </Heading>
  );
};

export default TodosHeading;
