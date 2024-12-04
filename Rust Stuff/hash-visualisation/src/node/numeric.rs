use nannou::rand::random_range;

use crate::grammar::{Grammar, Resolve};

use super::Evaluate;
use super::NodeBoolean;
use std::fmt::Display;
use std::fmt::Formatter;
use std::ops::*;
use std::result::Result;

pub enum NodeNumeric {
    Number(f32),
    Add(Box<NodeNumeric>, Box<NodeNumeric>),
    Mult(Box<NodeNumeric>, Box<NodeNumeric>),
    X,
    Y,
    If(Box<NodeBoolean>, Box<NodeNumeric>, Box<NodeNumeric>),
    Mod(Box<NodeNumeric>, Box<NodeNumeric>),
    Grammar(String),
    RandomNumber,
}

impl Display for NodeNumeric {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            NodeNumeric::Number(num) => write!(f, "{}", num),
            NodeNumeric::Add(first, second) => write!(f, "({} + {})", first, second),
            NodeNumeric::Mult(first, second) => write!(f, "({} * {})", first, second),
            NodeNumeric::X => write!(f, "x"),
            NodeNumeric::Y => write!(f, "y"),
            NodeNumeric::If(cond, then, other) => {
                write!(f, "If ({}) then ({}) else ({})", cond, then, other)
            }
            NodeNumeric::Mod(first, second) => write!(f, "({} mod {})", first, second),
            NodeNumeric::Grammar(s) => write!(f, "{}", s),
            NodeNumeric::RandomNumber => panic!("Random Number should not be printed"),
        }
    }
}

impl Evaluate for NodeNumeric {
    type Output = f32;
    fn eval(&self, x: &f32, y: &f32) -> Result<f32, &'static str> {
        match self {
            NodeNumeric::Number(num) => Ok(*num),
            NodeNumeric::Add(first, second) => {
                Ok(((first.eval(x, y)? + second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            NodeNumeric::Mult(first, second) => {
                Ok(((first.eval(x, y)? * second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            NodeNumeric::X => Ok(*x),
            NodeNumeric::Y => Ok(*y),
            NodeNumeric::If(cond, then, other) => {
                if cond.eval(x, y)? {
                    then.eval(x, y)
                } else {
                    other.eval(x, y)
                }
            }
            NodeNumeric::Mod(first, second) => Ok(first.eval(x, y)? % second.eval(x, y)?),
            NodeNumeric::Grammar(_) => Err("Grammar branch must not be evaluated"),
            NodeNumeric::RandomNumber => Err("Random number must not be evaluated"),
        }
    }
}

impl Resolve for NodeNumeric {
    fn resolve(self, grammar: Grammar, depth: usize) -> Result<Box<Self>, &'static str> {
        match self {
            Self::RandomNumber => Ok(Box::new(NodeNumeric::Number((random_range(-1.0, 1.0))))),
            NodeNumeric::Grammar(s) => {
                Err("not implemented")
                let list = grammar.branches.get(s);
                // If depth is zero then match s to the grammar and pick the first option, and
                // resolve it
                // If depth is greater than zero then match s to the grammar, pick a random option
                // based on chances, and resolve it
            }
            _ => Ok(Box::new(self)),
        }
    }
}

impl Add for NodeNumeric {
    type Output = NodeNumeric;
    fn add(self, other: Self) -> Self::Output {
        Self::Add(Box::new(self), Box::new(other))
    }
}

impl Mul for NodeNumeric {
    type Output = NodeNumeric;
    fn mul(self, other: Self) -> Self::Output {
        Self::Mult(Box::new(self), Box::new(other))
    }
}

impl Rem for NodeNumeric {
    type Output = NodeNumeric;
    fn rem(self, other: Self) -> Self::Output {
        Self::Mod(Box::new(self), Box::new(other))
    }
}
