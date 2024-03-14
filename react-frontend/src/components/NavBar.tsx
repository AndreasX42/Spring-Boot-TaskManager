import { HStack, Image, Text } from "@chakra-ui/react";
import logo from "../assets/logo.webp";
import ColorModeSwitch from "./ColorModeSwitch";
import SearchInput from "./SearchInput";

interface Props {
  onSearch: (searchText: string) => void;
}

const NavBar = ({ onSearch }: Props) => {
  return (
    <HStack padding="10px">
      <Image src={logo} boxSize="60px" />
      <Text
        fontSize="32px"
        whiteSpace="nowrap"
        //fontFamily="arial" // Choose a suitable font family
        fontWeight="bold" // Make it bold for emphasis
        //color="teal" // Set a color that fits your theme
        letterSpacing="1px" // Adjust letter spacing for style
        textTransform="capitalize" // Capitalize the text
        marginRight={10}
      >
        TaskManager
      </Text>
      <SearchInput onSearch={onSearch} />
      <ColorModeSwitch />
    </HStack>
  );
};

export default NavBar;
