import { Box } from "@chakra-ui/react";
import { ReactNode } from "react";

interface Props {
  children: ReactNode;
}

const TodoContainer = ({ children }: Props) => {
  return (
    <Box borderRadius={10} overflow="hidden" maxWidth={1000}>
      {children}
    </Box>
  );
};

export default TodoContainer;
