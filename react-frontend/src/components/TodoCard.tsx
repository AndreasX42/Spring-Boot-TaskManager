import { Game } from "../hooks/useTodos";
import {
  Card,
  CardBody,
  HStack,
  Heading,
  Text,
  useColorMode,
} from "@chakra-ui/react";

interface Props {
  todo: Game;
}

const TodoCard = ({ todo }: Props) => {
  const { colorMode } = useColorMode();

  return (
    <Card
      borderWidth={colorMode === "light" ? "1px" : "0"} // Apply border only in light mode
      borderColor="gray.300"
      borderRadius="s"
    >
      <HStack spacing={3} p={4}>
        <Text>Priority: x</Text>
        <Text>State: y</Text>
        <Text>Deadline: z</Text>
      </HStack>
      <CardBody>
        <Heading fontSize="1xl">{game.name}</Heading>
      </CardBody>
    </Card>
  );
};

export default TodoCard;
