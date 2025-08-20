import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface TabIconProps {
  name: 'home' | 'api' | 'profile';
  color: string;
  size?: number;
}

export function TabIcon({ name, color, size = 24 }: TabIconProps) {
  const getIcon = () => {
    switch (name) {
      case 'home':
        return '🏠';
      case 'api':
        return '⚙️';
      case 'profile':
        return '👤';
      default:
        return '📱';
    }
  };

  return (
    <View style={styles.container}>
      <Text style={[styles.icon, { color, fontSize: size }]}>
        {getIcon()}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  icon: {
    textAlign: 'center',
  },
});